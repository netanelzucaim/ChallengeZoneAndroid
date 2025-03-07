package com.idz.ChallengeZone.viewmodel

import androidx.lifecycle.ViewModel
import com.idz.ChallengeZone.model.Post

import androidx.lifecycle.LiveData
import com.idz.ChallengeZone.model.Model

class PostsListViewModel : ViewModel() {
    var posts: LiveData<List<Post>> = Model.shared.posts
    var postsOfLoggedUser: LiveData<List<Post>> = Model.shared.postsOfLoggedUser
    fun refreshAllPosts() {
        Model.shared.refreshAllPosts()
    }
}
