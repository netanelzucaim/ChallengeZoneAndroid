package com.idz.ChallengeZone//package com.idz.ChallengeZone

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.idz.ChallengeZone.databinding.FragmentEditPostBinding
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment() {
    private var binding: FragmentEditPostBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false
    var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = EditPostFragmentArgs.fromBundle(requireArguments())
        post = args.post
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        binding?.cancelButton?.setOnClickListener(::onCancelClicked)
        binding?.saveButton?.setOnClickListener(::onSaveClicked)
        binding?.deleteButton?.setOnClickListener(::onDeleteClicked)
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
        binding?.progressBar?.visibility = View.VISIBLE
        setPost()
        if (didSetProfileImage) {
            binding?.postPicImageView?.isDrawingCacheEnabled = true
            binding?.postPicImageView?.buildDrawingCache()
            val bitmap = (binding?.postPicImageView?.drawable as BitmapDrawable).bitmap

            Model.shared.updatePost(post!!, bitmap, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                val action = EditPostFragmentDirections.actionEditPostFragmentToPostListfragment()
                binding?.root?.let {
                    Navigation.findNavController(it).navigate(action)
                }
            }
        } else {
            Model.shared.updatePost(post!!, null, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                val action = EditPostFragmentDirections.actionEditPostFragmentToPostListfragment()
                binding?.root?.let {
                    Navigation.findNavController(it).navigate(action)
                }
            }
        }
    }

    private fun onCancelClicked(view: View) {
            val action = EditPostFragmentDirections.actionEditPostFragmentToPostListfragment()
            binding?.root?.let {
                Navigation.findNavController(it).navigate(action)
            }
    }
//
//    private fun onDeleteClicked(view: View){
//        binding?.progressBar?.visibility = View.VISIBLE
//        Model.shared.deletePost(post!!) {
//            binding?.progressBar?.visibility = View.GONE
//        }
//        Navigation.findNavController(view).popBackStack()
//        val action = EditPostFragmentDirections.actionEditPostFragmentToPostListfragment()
//        binding?.root?.let {
//            Navigation.findNavController(it).navigate(action)
//        }
//    }

    private fun setupView(view: View?) {
        binding?.contentEditText?.setText(post?.content)
//        binding?.idEditText?.setText(student?.id)
//        binding?.phoneEditText?.setText(student?.phone)
//        binding?.addressEditText?.setText(student?.address)
//        binding?.enabledCheckBox?.isChecked = student?.isChecked!!
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
}