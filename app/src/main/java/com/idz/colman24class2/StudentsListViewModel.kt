package com.idz.colman24class2

import androidx.lifecycle.ViewModel
import com.idz.colman24class2.model.Student

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
//package com.idz.colman24class2
//import androidx.lifecycle.ViewModel
//import com.idz.colman24class2.model.Student
//class StudentsListViewModel: ViewModel() {
//    var students: List<Student>? = null
//}