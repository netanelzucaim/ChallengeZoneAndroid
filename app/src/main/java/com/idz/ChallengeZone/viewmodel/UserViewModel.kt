package com.idz.ChallengeZone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.User

class UserViewModel : ViewModel() {

    var user: LiveData<User?> = MutableLiveData()
    fun fetchUser(): LiveData<User?> {
        user = Model.shared.getLoggedUser()
        return user
    }
}