package com.idz.ChallengeZone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.idz.ChallengeZone.R
import com.idz.ChallengeZone.model.Student

import  com.idz.ChallengeZone.OnItemClickListener
import com.idz.ChallengeZone.databinding.StudentListRowBinding

//class StudentsRecyclerAdapter(private val students: MutableList<Student>?): RecyclerView.Adapter<StudentViewHolder>() {
class StudentsRecyclerAdapter(private var students: List<Student>?): RecyclerView.Adapter<StudentViewHolder>() {


        var listener: OnItemClickListener? = null
        fun update(students: List<Student>?) {
            this.students = students
        }


    fun set(students: List<Student>?) {
            this.students = students
        }
        override fun getItemCount(): Int = students?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val inflaor = LayoutInflater.from(parent.context)
            val binding = StudentListRowBinding.inflate(inflaor, parent, false)

            return StudentViewHolder(binding, listener)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            holder.bind(
                student = students?.get(position),
                position = position
            )
        }
    }