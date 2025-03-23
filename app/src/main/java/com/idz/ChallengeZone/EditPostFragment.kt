package com.idz.ChallengeZone

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.idz.ChallengeZone.databinding.FragmentEditPostBinding
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import com.idz.ChallengeZone.viewmodel.PostViewModel
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment() {
    private var binding: FragmentEditPostBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false
    private val postViewModel: PostViewModel by viewModels()
    private var sourceScreen: String? = null
    var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = EditPostFragmentArgs.fromBundle(requireArguments())
        post = args.post
        sourceScreen = args.sourceScreen
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        binding?.returnButton?.setOnClickListener(::onCancelClicked)
        binding?.saveButton?.setOnClickListener(::onSaveClicked)
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.postPicImageView?.setImageBitmap(bitmap)
            didSetProfileImage = true
        }
        binding?.takePhotoButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }
        setupView(binding?.root)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun onSaveClicked(view: View) {
        if (binding?.contentEditText?.text?.isEmpty() == true && !didSetProfileImage) {
            makeAToast("Please enter a post content or select a picture")
            return
        }

        binding?.progressBar?.visibility = View.VISIBLE
        setPost()
        if (didSetProfileImage) {
            binding?.postPicImageView?.isDrawingCacheEnabled = true
            binding?.postPicImageView?.buildDrawingCache()
            val bitmap = (binding?.postPicImageView?.drawable as BitmapDrawable).bitmap

            postViewModel.updatePost(post!!, bitmap, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                navigateToPostList(view)
            }
        } else {
            postViewModel.updatePost(post!!, null, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                navigateToPostList(view)
            }
        }
    }

    private fun navigateToPostList(view: View) {
        val navController = Navigation.findNavController(view)
        when (sourceScreen) {
            "profile" -> {
                navController.popBackStack(R.id.editPostFragment, true)
                navController.popBackStack(R.id.profileFragment, false)
            }
            "home" -> {
                navController.popBackStack(R.id.editPostFragment, true)
                navController.popBackStack(R.id.postListFragment, false)
            }
        }
    }

    private fun onCancelClicked(view: View) {
        navigateToPostList(view)
    }

    private fun setupView(view: View?) {
        binding?.contentEditText?.setText(post?.content)
        post?.postPic?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.avatar)
                    .into(binding?.postPicImageView)
            }
        }
    }

    private fun setPost() {
        post = Post(
            id = post!!.id,
            sender = post!!.sender,
            content = binding?.contentEditText?.text.toString(),
            postPic = post?.postPic
        )

    }

    private fun makeAToast(text: String?) {
        AlertDialog.Builder(context)
            .setTitle("Notification")
            .setMessage(text)
            .create().show()
    }
}