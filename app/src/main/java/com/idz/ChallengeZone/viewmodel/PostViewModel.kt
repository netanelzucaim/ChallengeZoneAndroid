package com.idz.ChallengeZone.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post

class PostViewModel : ViewModel() {
    var posts: LiveData<List<Post>> = Model.shared.posts
    var postsOfLoggedUser: LiveData<List<Post>> = Model.shared.postsOfLoggedUser
    var loadingState: LiveData<Model.LoadingState> = Model.shared.loadingState

    fun refreshAllPosts() {
        Model.shared.refreshAllPosts()
    }

    fun addPost(post: Post, bitmap: Bitmap?, storage: Model.Storage, callback: () -> Unit) {
        Model.shared.addPost(post, bitmap, storage, callback)
    }

    fun updatePost(post: Post, bitmap: Bitmap?, storage: Model.Storage, callback: () -> Unit) {
        Model.shared.updatePost(post, bitmap, storage, callback)
    }

    fun deletePost(post: Post, callback: () -> Unit) {
        Model.shared.deletePost(post, callback)
    }
}