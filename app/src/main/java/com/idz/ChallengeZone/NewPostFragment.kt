package com.idz.ChallengeZone

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import com.idz.ChallengeZone.databinding.FragmentNewPostBinding


class NewPostFragment : Fragment() {
    private var binding: FragmentNewPostBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

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
    val post = Post(
        id =  "1",
        sender = Model.shared.username.toString(),
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
            Navigation.findNavController(view).popBackStack()
        }
    } else {
        Model.shared.addPost(post, null, Model.Storage.CLOUDINARY) {
            binding?.progressBar?.visibility = View.GONE
            Navigation.findNavController(view).popBackStack()
        }
    }
}
}