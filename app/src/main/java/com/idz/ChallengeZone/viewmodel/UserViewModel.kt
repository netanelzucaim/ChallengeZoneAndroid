package com.idz.ChallengeZone.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.User

class UserViewModel : ViewModel() {

    var user: LiveData<User?> = MutableLiveData()
    var loadingState: LiveData<Model.LoadingState> = Model.shared.loadingState


    fun fetchUser(lifecycleOwner: LifecycleOwner): LiveData<User?> {
        user = Model.shared.getLoggedUser(lifecycleOwner = lifecycleOwner)
        return user
    }

    fun refreshAllUsers() {
        Model.shared.refreshAllUsers()
    }

    fun updateUser(user: User, bitmap: Bitmap?, storage: Model.Storage, callback: () -> Unit) {
        Model.shared.updateUser(user, bitmap, storage, callback)
    }

    fun getOtherUser(lifecycleOwner: LifecycleOwner, userId: String?): LiveData<User?> {
        return Model.shared.getOtherUser(lifecycleOwner, userId)
    }
}