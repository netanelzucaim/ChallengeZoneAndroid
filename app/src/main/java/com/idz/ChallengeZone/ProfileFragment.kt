package com.idz.ChallengeZone

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.ChallengeZone.adapter.postAdapter.PostsRecyclerAdapter
import com.idz.ChallengeZone.databinding.FragmentPostsListBinding
import com.idz.ChallengeZone.databinding.FragmentProfileBinding
import com.idz.ChallengeZone.model.User
import com.idz.ChallengeZone.viewmodel.AuthViewModel
import com.idz.ChallengeZone.viewmodel.PostViewModel
import com.idz.ChallengeZone.viewmodel.UserViewModel
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private val postsViewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private var adapter: PostsRecyclerAdapter? = null
    private var binding: FragmentProfileBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false
    private var isEditing = false
    var user: User? = null

    private var originalUserName: String? = null
    private var originalAvatarUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding?.recyclerView?.setHasFixedSize(true)
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)

        adapter = PostsRecyclerAdapter(postsViewModel.postsOfLoggedUser.value, viewLifecycleOwner, "profile")
        binding?.recyclerView?.adapter = adapter

        postsViewModel.postsOfLoggedUser.observe(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
        binding?.swipeToRefresh?.setOnRefreshListener(postsViewModel::refreshAllPostsOfLoggedUser)
        postsViewModel.loadingState.observe(viewLifecycleOwner) { state ->
            binding?.swipeToRefresh?.isRefreshing = state == Model.LoadingState.LOADING
        }
        Log.d("TAG", "profile")

        binding?.saveButton?.setOnClickListener {
            if (isEditing) {
                onSaveClicked(it)
            } else {
                enableEditing()
            }
        }

        binding?.logoutButton?.setOnClickListener {
            if (isEditing) {
                cancelEditing()
            } else {
                onLogoutClicked(it)
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.avatarImageView?.setImageBitmap(bitmap)
            didSetProfileImage = true
        }

        binding?.takePhotoButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        setupView()
        setupRecyclerView()

        adapter?.listener = object : OnItemClickListenerPosts {
            override fun onItemClick(position: Int) {
                Log.d("TAG", "On click Activity listener on position $position")
            }

            override fun onItemClick(post: Post?) {
                // Handle item click
            }
        }
        binding?.recyclerView?.adapter = adapter

        return binding?.root
    }

    private fun setupRecyclerView() {
        binding?.recyclerView?.setHasFixedSize(true)
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(postsViewModel.postsOfLoggedUser.value, viewLifecycleOwner, "profile")
        postsViewModel.postsOfLoggedUser.observeOnce(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
        binding?.swipeToRefresh?.setOnRefreshListener(postsViewModel::refreshAllPosts)
        binding?.recyclerView?.adapter = adapter
        disableEditing()
    }

    private fun setupView() {
        userViewModel.fetchUser(viewLifecycleOwner).observeOnce(viewLifecycleOwner) { loggedUser ->
            user = loggedUser
            user?.userName?.let { userName ->
                binding?.userNameEditText?.setText(userName)
            }
            user?.avatarUrl?.let { avatarUrl ->
                if (avatarUrl.isNotBlank()) {
                    Picasso.get()
                        .load(avatarUrl)
                        .placeholder(R.drawable.avatar)
                        .into(binding?.avatarImageView)
                }
            }

            originalUserName = user?.userName
            originalAvatarUrl = user?.avatarUrl

            disableEditing()
        }
    }

    private fun enableEditing() {
        originalUserName = binding?.userNameEditText?.text.toString()
        originalAvatarUrl = user?.avatarUrl

        binding?.userNameEditText?.isEnabled = true
        binding?.takePhotoButton?.isEnabled = true
        binding?.takePhotoButton?.isVisible = true
        binding?.saveButton?.text = "Save"
        isEditing = true
        updateLogoutButton()
    }

    private fun disableEditing() {
        binding?.userNameEditText?.isEnabled = false
        binding?.takePhotoButton?.isEnabled = false
        binding?.takePhotoButton?.isVisible = false
        binding?.saveButton?.text = "Edit"
        isEditing = false
        updateLogoutButton()
    }

    private fun updateLogoutButton() {
        if (isEditing) {
            binding?.logoutButton?.text = "Cancel"
        } else {
            binding?.logoutButton?.text = "Logout"
        }
    }

    private fun cancelEditing() {
        binding?.userNameEditText?.setText(originalUserName)
        user?.avatarUrl = originalAvatarUrl.toString()
        Picasso.get()
            .load(user?.avatarUrl)
            .placeholder(R.drawable.avatar)
            .into(binding?.avatarImageView)

        disableEditing()
    }

    private fun onSaveClicked(view: View) {
        binding?.progressBar?.visibility = View.VISIBLE
        authViewModel.checkUsernameTaken(binding?.userNameEditText?.text.toString())

        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.isUsernameTaken.observeOnce(viewLifecycleOwner) { isTaken ->
            if (isTaken == true && binding?.userNameEditText?.text.toString() != user?.userName) {
                binding?.progressBar?.visibility = View.GONE
                makeAToast("Username already been taken")
            } else {
                updateUser()
            }
        }
    }

    private fun updateUser() {
        setUser()
        if (didSetProfileImage) {
            binding?.avatarImageView?.isDrawingCacheEnabled = true
            binding?.avatarImageView?.buildDrawingCache()
            val bitmap = (binding?.avatarImageView?.drawable as BitmapDrawable).bitmap

            userViewModel.updateUser(user!!, bitmap, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                getAllPosts()
                disableEditing()
            }
        } else {
            userViewModel.updateUser(user!!, null, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                getAllPosts()
                disableEditing()
            }
        }
    }

    private fun onLogoutClicked(view: View) {
        authViewModel.logOut()
        val navController = Navigation.findNavController(view)
        val action = ProfileFragmentDirections.actionGlobalSignInFragment()
        navController.navigate(action)
    }

    override fun onResume() {
        super.onResume()
        postsViewModel.refreshAllPostsOfLoggedUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getAllPosts() {
        binding?.progressBar?.visibility = View.VISIBLE
        postsViewModel.refreshAllPosts()
        postsViewModel.postsOfLoggedUser.observeOnce(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
    }

    private fun setUser() {
        user = user?.copy(
            id = user?.id ?: "",
            userName = binding?.userNameEditText?.text.toString(),
            password = user?.password ?: "",
            avatarUrl = user?.avatarUrl ?: "",
            email = user?.email ?: ""
        )
    }

    private fun makeAToast(text: String?) {
        AlertDialog.Builder(context)
            .setTitle("Notification")
            .setMessage(text)
            .create().show()
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }
}