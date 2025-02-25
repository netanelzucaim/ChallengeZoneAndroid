package com.idz.ChallengeZone

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.idz.ChallengeZone.databinding.FragmentNewPostBinding
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import com.idz.ChallengeZone.model.User
import java.util.UUID


class NewPostFragment : Fragment() {
    private var binding: FragmentNewPostBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false
    private val userViewModel: UserViewModel by viewModels()
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userViewModel.fetchUser()?.observe(viewLifecycleOwner) { newUser ->
            Log.d("TAG", "current logged User is: $user")
            user = newUser
        }


        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding?.btnCreatePost?.setOnClickListener(::onSaveClicked)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.postPicImageView?.setImageBitmap(bitmap)
            didSetProfileImage = true

        }

        binding?.takePhotoButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        return binding?.root

    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

private fun onSaveClicked(view: View) {
    var currentUser = ""
    Model.shared.getLoggedUser()?.observe(viewLifecycleOwner) { user ->
        currentUser = user?.id ?: ""
        Log.d("TAG","current user after  observer is $currentUser")
        val post = Post(
            id  = UUID.randomUUID().toString(),
            sender = currentUser,
            content = binding?.contentEditText?.text?.toString() ?: "",
            postPic = ""
        )
        binding?.progressBar?.visibility = View.VISIBLE
        if (didSetProfileImage) {
            binding?.postPicImageView?.isDrawingCacheEnabled = true
            binding?.postPicImageView?.buildDrawingCache()
            val bitmap = (binding?.postPicImageView?.drawable as BitmapDrawable).bitmap

            Model.shared.addPost(post, bitmap, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                val action = NewPostFragmentDirections.actionNewPostFragmentToPostListFragment()
                binding?.root?.let {
                    Navigation.findNavController(it).navigate(action)
                }
            }
        } else {
            Model.shared.addPost(post, null, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                val action = NewPostFragmentDirections.actionNewPostFragmentToPostListFragment()
                binding?.root?.let {
                    Navigation.findNavController(it).navigate(action)
                }
            }
        }
    }
    }
}