package com.idz.ChallengeZone.studentFragments//package com.idz.ChallengeZone
//
//import android.graphics.drawable.BitmapDrawable
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.CheckBox
//import android.widget.EditText
//import android.widget.TextView
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.navigation.Navigation
//import com.idz.ChallengeZone.model.Model
//import com.idz.ChallengeZone.model.Student
//import com.idz.ChallengeZone.databinding.FragmentAddStudentBinding
//
//
//class AddStudentFragment : Fragment() {
//    private var binding: FragmentAddStudentBinding? = null
//    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
//    private var didSetProfileImage = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        menu.clear()
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentAddStudentBinding.inflate(inflater, container, false)
//        binding?.cancelButton?.setOnClickListener(::onCancelClicked)
//        binding?.saveButton?.setOnClickListener(::onSaveClicked)
//
//        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
//            binding?.imageView?.setImageBitmap(bitmap)
//            didSetProfileImage = true
//
//        }
//
//        binding?.takePhotoButton?.setOnClickListener {
//            cameraLauncher?.launch(null)
//        }
//
//
//        return binding?.root
//
//    }
//    override fun onDestroy() {
//        super.onDestroy()
//        binding = null
//    }
//
//private fun onSaveClicked(view: View) {
//    val student = Student(
//        name = binding?.nameEditText?.text?.toString() ?: "",
//        id = binding?.idEditText?.text?.toString() ?: "",
//        phone =  binding?.phoneEditText?.text?.toString() ?: "",
//        address= binding?.addressEditText?.text?.toString() ?: "",
//        isChecked = binding?.enabledCheckBox?.isChecked ?: false,
//        avatarUrl = ""
//    )
//    binding?.progressBar?.visibility = View.VISIBLE
//    if (didSetProfileImage) {
//        binding?.imageView?.isDrawingCacheEnabled = true
//        binding?.imageView?.buildDrawingCache()
//        val bitmap = (binding?.imageView?.drawable as BitmapDrawable).bitmap
//
//        Model.shared.add(student, bitmap, Model.Storage.CLOUDINARY) {
//            binding?.progressBar?.visibility = View.GONE
//            Navigation.findNavController(view).popBackStack()
//        }
//    } else {
//        Model.shared.add(student, null, Model.Storage.CLOUDINARY) {
//            binding?.progressBar?.visibility = View.GONE
//            Navigation.findNavController(view).popBackStack()
//        }
//    }
//
//}
//    private fun onCancelClicked(view: View) {
//        Navigation.findNavController(view).popBackStack()
//    }
//}