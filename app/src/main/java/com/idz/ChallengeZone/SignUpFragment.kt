package com.idz.ChallengeZone

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseUser
import com.idz.ChallengeZone.databinding.FragmentSignUpBinding
import com.idz.ChallengeZone.model.EmptyCallback
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.User
import java.util.regex.Pattern
public typealias EmptyCallback = () -> Unit

class SignUpFragment : Fragment() {

    private var binding: FragmentSignUpBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        setupRegisterButton()
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.imageView?.setImageBitmap(bitmap)
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

    private fun setupRegisterButton() {
        binding?.registeredButton?.setOnClickListener {
            signUpFunction()
        }
    }
    private fun signUpFunction() {
        val username = binding?.usernameInput?.text.toString()
        val password = binding?.passwordInput?.text.toString()
        val email = binding?.emailInput?.text.toString()
        val newUser = User(username, password, "", email)

        if (validateInput()) {
            binding?.progressBar?.visibility = View.VISIBLE
            Model.shared.isUserNameTaken(username) { isTaken ->
                if (isTaken) {
                    binding?.progressBar?.visibility = View.GONE
                    makeAToast("Username already been taken")
                } else {
                    Model.shared.isEmailTaken(email) { isEmailTaken ->
                        if (isEmailTaken) {
                            binding?.progressBar?.visibility = View.GONE
                            makeAToast("Email already been taken")
                        } else {
                            if (didSetProfileImage) {
                                binding?.imageView?.isDrawingCacheEnabled = true
                                binding?.imageView?.buildDrawingCache()
                                val bitmap = (binding?.imageView?.drawable as BitmapDrawable).bitmap

                                Model.shared.signUp(newUser, password, bitmap, Model.Storage.CLOUDINARY) {
                                    binding?.progressBar?.visibility = View.GONE
                                }
                            } else {
                                Model.shared.signUp(newUser, password, null, Model.Storage.CLOUDINARY) {
                                    binding?.progressBar?.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun makeAToast(text: String?) {
        AlertDialog.Builder(context)
            .setTitle("Invalid Input")
            .setMessage(text)
            .setPositiveButton("Ok") { dialog: DialogInterface?, which: Int -> }
            .create().show()
    }

    fun validateInput(): Boolean {
        val username = binding?.usernameInput?.text.toString()
        val email = binding?.emailInput?.text.toString()
        val password = binding?.passwordInput?.text.toString()

        if (username.isEmpty()) {
            makeAToast("Please enter a username")
            return false
        } else if (!isValidEmail(email)) {
            makeAToast("Please enter a valid email")
            return false
        } else if (password.length < 6) {
            makeAToast("Password must contain at least 6 characters")
            return false
        }

        return true
    }

    fun isValidEmail(email: String?): Boolean {
        val regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    private fun reload() {
    }
}