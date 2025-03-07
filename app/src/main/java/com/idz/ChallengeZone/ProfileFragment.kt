package com.idz.ChallengeZone

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import  android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.ChallengeZone.adapter.postAdapter.PostsRecyclerAdapter
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.idz.ChallengeZone.databinding.FragmentProfileBinding
import com.idz.ChallengeZone.model.User
import com.idz.ChallengeZone.viewmodel.PostsListViewModel
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private val viewModel: PostsListViewModel by viewModels()
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_posts_list, container, false)

        binding?.recyclerView?.setHasFixedSize(true)
        setupView(view)
        val layoutManager = LinearLayoutManager(context)

        binding?.recyclerView?.layoutManager = layoutManager

        adapter = PostsRecyclerAdapter(viewModel.postsOfLoggedUser.value)
        viewModel.postsOfLoggedUser.observe(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
        binding?.swipeToRefresh?.setOnRefreshListener(viewModel::refreshAllPosts)
        Model.shared.loadingState.observe(viewLifecycleOwner) { state ->
            binding?.swipeToRefresh?.isRefreshing = state == Model.LoadingState.LOADING
        }

        adapter?.listener = object : OnItemClickListenerPosts{
            override fun onItemClick(position: Int) {
                Log.d("TAG", "On click Activity listener on position $position")
            }

            override fun onItemClick(post: Post?) {
                post?.let {
                }
            }
        }

        binding?.recyclerView?.adapter = adapter

        return binding?.root
    }

    private fun onSaveClicked(view: View) {
        binding?.progressBar?.visibility = View.VISIBLE
        Model.shared.isUserNameTaken(binding?.userNameEditText?.text.toString()) { isTaken ->
            if (isTaken && binding?.userNameEditText?.text.toString() != user?.userName) {
                binding?.progressBar?.visibility = View.GONE
                makeAToast("Username already been taken")
            } else {
                    setUser()
                    if (didSetProfileImage) {
                        binding?.avatarImageView?.isDrawingCacheEnabled = true
                        binding?.avatarImageView?.buildDrawingCache()
                        val bitmap = (binding?.avatarImageView?.drawable as BitmapDrawable).bitmap

                        Model.shared.updateUser(user!!, bitmap, Model.Storage.CLOUDINARY) {
                            binding?.progressBar?.visibility = View.GONE
                            getAllPosts() // Refresh the posts list
                        }
                    } else {
                        Model.shared.updateUser(user!!, null, Model.Storage.CLOUDINARY) {
                            binding?.progressBar?.visibility = View.GONE
                            getAllPosts() // Refresh the posts list
                        }
                    }
                }
        }
    }

    private fun setupView(view: View?) {
        Model.shared.getLoggedUser().observeForever { loggedUser ->
            user = loggedUser
            Log.d("TAG", "User from dao avatarUrl is: ${user?.json}")
            user?.userName?.let { userName ->
                binding?.userNameEditText?.setText(userName)
            }
//            user?.avatarUrl?.let {
//                if (it.isNotBlank()) {
//                    Picasso.get()
//                        .load(it)
//                        .placeholder(R.drawable.avatar)
//                        .into(binding?.avatarImageView)
//                }
//            }
        }
    }

private fun onLogoutClicked(view: View) {
    Model.shared.logOut()
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
    viewModel.refreshAllPosts()
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
    fun makeAToast(text: String?, navigate: Boolean = false) {
        AlertDialog.Builder(context)
            .setTitle("Notification")
            .setMessage(text)
            .create().show()
    }



}