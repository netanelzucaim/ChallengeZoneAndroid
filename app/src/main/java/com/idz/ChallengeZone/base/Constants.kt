package com.idz.ChallengeZone.base

import com.idz.ChallengeZone.model.Post


typealias PostsCallback = (List<Post>) -> Unit
typealias EmptyCallback = () -> Unit

object Constants {

    object Collections {
        const val USERS = "users_collection"
        const val POSTS = "posts_collection"

    }
}