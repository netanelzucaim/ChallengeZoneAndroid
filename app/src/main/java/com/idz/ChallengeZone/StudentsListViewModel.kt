package com.idz.ChallengeZone

import androidx.lifecycle.ViewModel
import com.idz.ChallengeZone.model.Student

class StudentsListViewModel : ViewModel() {
     var students: List<Student>? = null
//    var students: List<Student>?
//        get() = students
//        set(value) {
//            students = value
//        }
    fun updateStudents(students: List<Student>){
        this.students = students
    }
}
//package com.idz.ChallengeZone
//import androidx.lifecycle.ViewModel
//import com.idz.ChallengeZone.model.Student
//class StudentsListViewModel: ViewModel() {
//    var students: List<Student>? = null
//}