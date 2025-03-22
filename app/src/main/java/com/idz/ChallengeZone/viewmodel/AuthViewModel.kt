package com.idz.ChallengeZone.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.User

class AuthViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean?>()
    val loginResult: LiveData<Boolean?> get() = _loginResult

    private val _signUpResult = MutableLiveData<Boolean?>()
    val signUpResult: LiveData<Boolean?> get() = _signUpResult

    private val _isUsernameTaken = MutableLiveData<Boolean?>()
    val isUsernameTaken: LiveData<Boolean?> get() = _isUsernameTaken

    private val _isEmailTaken = MutableLiveData<Boolean?>()
    val isEmailTaken: LiveData<Boolean?> get() = _isEmailTaken

    fun logIn(username: String, password: String) {
        Model.shared.logIn(username, password) { isSuccessful ->
            _loginResult.value = isSuccessful
        }
    }

    fun signUp(newUser: User, password: String, bitmap: Bitmap?, storage: Model.Storage) {
        Model.shared.signUp(newUser, password, bitmap, storage) {
            _signUpResult.value = true
        }
    }

    fun checkUsernameTaken(username: String) {
        Model.shared.isUserNameTaken(username) { isTaken ->
            _isUsernameTaken.value = isTaken
        }
    }

    fun checkEmailTaken(email: String) {
        Model.shared.isEmailTaken(email) { isTaken ->
            _isEmailTaken.value = isTaken
        }
    }

    fun logOut() {
        Model.shared.logOut()
    }

    fun getLoggedUser(viewLifecycleOwner: LifecycleOwner): LiveData<User?> {
        return Model.shared.getLoggedUser(lifecycleOwner = viewLifecycleOwner)
    }
}