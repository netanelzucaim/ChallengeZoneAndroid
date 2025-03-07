package com.idz.ChallengeZone

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.idz.ChallengeZone.databinding.FragmentSignInBinding
import com.idz.ChallengeZone.viewmodel.AuthViewModel

class SignInFragment : Fragment() {

    private var binding: FragmentSignInBinding? = null
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        setupSignInButton()
        setupSignUpButton()
        observeViewModel()
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
            binding?.progressBar?.visibility = View.VISIBLE
            authViewModel.logIn(username, password)
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

    private fun observeViewModel() {
        authViewModel.loginResult.observe(viewLifecycleOwner) { isSuccessful ->
            binding?.progressBar?.visibility = View.GONE
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

    private fun makeAToast(text: String?) {
        AlertDialog.Builder(context)
            .setTitle("Invalid Input")
            .setMessage(text)
            .setPositiveButton("Ok") { dialog: DialogInterface?, which: Int -> }
            .create().show()
    }
}