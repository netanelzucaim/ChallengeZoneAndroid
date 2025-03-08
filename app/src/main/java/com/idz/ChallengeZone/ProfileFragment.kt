package com.idz.ChallengeZone

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.ChallengeZone.adapter.postAdapter.PostsRecyclerAdapter
import com.idz.ChallengeZone.databinding.FragmentProfileBinding
import com.idz.ChallengeZone.model.User
import com.idz.ChallengeZone.viewmodel.AuthViewModel
import com.idz.ChallengeZone.viewmodel.PostViewModel
import com.idz.ChallengeZone.viewmodel.UserViewModel
import com.idz.ChallengeZone.model.Model

class ProfileFragment : Fragment() {

    private val postsViewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private var adapter: PostsRecyclerAdapter? = null
    private var binding: FragmentProfileBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false
    var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding?.saveButton?.setOnClickListener(::onSaveClicked)
        binding?.logoutButton?.setOnClickListener(::onLogoutClicked)
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.avatarImageView?.setImageBitmap(bitmap)
            didSetProfileImage = true
        }
        binding?.takePhotoButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        setupView()
        setupRecyclerView()

        observeViewModel()

        return binding?.root
    }

    private fun setupRecyclerView() {
        binding?.recyclerView?.setHasFixedSize(true)
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(postsViewModel.postsOfLoggedUser.value)
        postsViewModel.postsOfLoggedUser.observe(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
        binding?.swipeToRefresh?.setOnRefreshListener(postsViewModel::refreshAllPosts)
        binding?.recyclerView?.adapter = adapter
    }

    private fun setupView() {
        userViewModel.fetchUser().observe(viewLifecycleOwner) { loggedUser ->
            user = loggedUser
            user?.userName?.let { userName ->
                binding?.userNameEditText?.setText(userName)
            }
        }
    }

    private fun observeViewModel() {
        authViewModel.isUsernameTaken.observe(viewLifecycleOwner) { isTaken ->
            if (isTaken == true && binding?.userNameEditText?.text.toString() != user?.userName) {
                binding?.progressBar?.visibility = View.GONE
                makeAToast("Username already been taken")
            } else {
                updateUser()
            }
        }
    }

    private fun onSaveClicked(view: View) {
        binding?.progressBar?.visibility = View.VISIBLE
        authViewModel.checkUsernameTaken(binding?.userNameEditText?.text.toString())
    }

    private fun updateUser() {
        setUser()
        if (didSetProfileImage) {
            binding?.avatarImageView?.isDrawingCacheEnabled = true
            binding?.avatarImageView?.buildDrawingCache()
            val bitmap = (binding?.avatarImageView?.drawable as BitmapDrawable).bitmap

            userViewModel.updateUser(user!!, bitmap, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                getAllPosts() // Refresh the posts list
            }
        } else {
            userViewModel.updateUser(user!!, null, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                getAllPosts() // Refresh the posts list
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
        getAllPosts()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getAllPosts() {
        binding?.progressBar?.visibility = View.VISIBLE
        postsViewModel.refreshAllPosts()
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
}