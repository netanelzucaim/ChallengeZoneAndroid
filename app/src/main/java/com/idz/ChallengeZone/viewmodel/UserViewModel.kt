package com.idz.ChallengeZone.viewmodel

import android.graphics.Bitmap
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

    fun updateUser(user: User, bitmap: Bitmap?, storage: Model.Storage, callback: () -> Unit) {
        Model.shared.updateUser(user, bitmap, storage, callback)
    }

    fun getOtherUser(userId: String?): LiveData<User?> {
        return Model.shared.getOtherUser(userId)
    }
}