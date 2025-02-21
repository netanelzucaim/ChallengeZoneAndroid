package com.idz.ChallengeZone

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.idz.ChallengeZone.databinding.FragmentAddStudentBinding
import com.idz.ChallengeZone.databinding.FragmentSignInBinding
import com.squareup.picasso.Picasso
import androidx.navigation.fragment.findNavController
import com.idz.ChallengeZone.model.Model


class SignInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private var binding: FragmentSignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        setupSignInButton()
        setupSignUpButton()
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupSignInButton() {
        binding?.continueButton?.setOnClickListener {
            val username = binding?.usernameInput?.text.toString()
            val password = binding?.passwordInput?.text.toString()

            Model.shared.logIn(username, password) { isSuccessful ->
                if (isSuccessful == true) {
                    val action = SignInFragmentDirections.actionSignInFragmentToPostsListFragment()
                    binding?.root?.let {
                        Navigation.findNavController(it).navigate(action)
                    }
                } else {
                    makeAToast("Username or password are not correct")
                }
            }
        }
    }
    private fun setupSignUpButton() {
        binding?.signUpButton?.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            binding?.root?.let {
                Navigation.findNavController(it).navigate(action)
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

    //    private fun signIn(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                    // Navigate to StudentsListFragment
//                    val action = SignInFragmentDirections.actionSignInFragmentToStudentsListFragment()
//                    binding?.root?.let {
//                        Navigation.findNavController(it).navigate(action)
//                    }
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        requireContext(),
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    updateUI(null)
//                }
//            }
//    }
    private fun updateUI(user: FirebaseUser?) {
    }

    private fun reload() {
    }
}
