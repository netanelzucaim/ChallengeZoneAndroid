package com.idz.ChallengeZone.base

import com.idz.ChallengeZone.model.Post
import com.idz.ChallengeZone.model.Student

typealias StudentsCallback = (List<Student>) -> Unit
typealias PostsCallback = (List<Post>) -> Unit
typealias EmptyCallback = () -> Unit

object Constants {

    object Collections {
        const val STUDENTS = "students_collection"
        const val USERS = "users_collection"
        const val POSTS = "posts_collection"

    }
}