package com.idz.ChallengeZone.base

import com.idz.ChallengeZone.model.Student

typealias StudentsCallback = (List<Student>) -> Unit
typealias EmptyCallback = () -> Unit

object Constants {

    object Collections {
        const val STUDENTS = "students_collection"
    }
}