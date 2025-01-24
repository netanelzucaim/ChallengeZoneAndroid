package com.idz.colman24class2

import  android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idz.colman24class2.adapter.StudentsRecyclerAdapter
import com.idz.colman24class2.model.Model
import com.idz.colman24class2.model.Student
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.idz.colman24class2.databinding.FragmentStudentsListBinding

interface OnItemClickListener {
    fun onItemClick(position: Int)
    fun onItemClick(student: Student?)
}

class StudentsListFragment : Fragment() {
//    private var students: List<Student>? = null
    private var viewModel: StudentsListViewModel? = null
    private var adapter: StudentsRecyclerAdapter? = null
    private var binding: FragmentStudentsListBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentsListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[StudentsListViewModel::class.java]

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_students_list, container, false)
//
//       students = Model.shared.students
//
//        val recyclerView: RecyclerView = view.findViewById(R.id.students_list_fragment_recycler_view)
//        recyclerView.setHasFixedSize(true)
        binding?.recyclerView?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
//        recyclerView.layoutManager = layoutManager
//        val adapter = StudentsRecyclerAdapter(students)
        binding?.recyclerView?.layoutManager = layoutManager

        adapter = StudentsRecyclerAdapter(viewModel?.students)


        adapter?.listener = object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                Log.d("TAG", "On click Activity listener on position $position")
            }

            override fun onItemClick(student: Student?) {
//
//                val action = StudentsListFragmentDirections.actionStudentsListFragmentToStudentDetailsFragment(student!!)
//                Navigation.findNavController(view).navigate(action)


                student?.let {
                    val action = StudentsListFragmentDirections.actionStudentsListFragmentToStudentDetailsFragment(it)
                    binding?.root?.let {
                        Navigation.findNavController(it).navigate(action)
                    }
                }
            }
        }
//        recyclerView.adapter = adapter
        binding?.recyclerView?.adapter = adapter

//        val action = StudentsListFragmentDirections.actionGlobalAddStudentFragment()
//        binding?.addStudentButton?.setOnClickListener(Navigation.createNavigateOnClickListener(action))
        return binding?.root
    }


override fun onResume() {
    super.onResume()
    getAllStudents()
}
override fun onDestroy() {
    super.onDestroy()
    binding = null
}
private fun getAllStudents() {
    binding?.progressBar?.visibility = View.VISIBLE
    Model.shared.getAllStudents {
        viewModel?.updateStudents(it)
        adapter?.set(it)
        adapter?.notifyDataSetChanged()

        binding?.progressBar?.visibility = View.GONE
    }
}


}