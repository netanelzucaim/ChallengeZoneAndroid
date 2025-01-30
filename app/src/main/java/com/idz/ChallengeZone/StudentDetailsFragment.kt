package com.idz.ChallengeZone
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.navigation.Navigation
import com.idz.ChallengeZone.databinding.FragmentAddStudentBinding
import com.idz.ChallengeZone.model.Student
import com.idz.ChallengeZone.databinding.FragmentStudentDetailsBinding
import com.squareup.picasso.Picasso

class StudentDetailsFragment : Fragment() {

    private var binding: FragmentStudentDetailsBinding? = null

    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//         student = arguments?.let { StudentDetailsFragmentArgs.fromBundle(it) }

        val args = StudentDetailsFragmentArgs.fromBundle(requireArguments())
        student = args.student
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStudentDetailsBinding.inflate(inflater, container, false)
        setupView(binding?.root)
        return binding?.root

    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupView(view: View?) {
        binding?.nameTextView?.text = student!!.name
        binding?.idTextView?.text = student!!.id
        binding?.phoneTextView?.text = student!!.phone
        binding?.addressTextView?.text = student!!.address
        binding?.enabledCheckBox?.isChecked = student!!.isChecked
        student?.avatarUrl?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.avatar)
                    .into(binding?.imageView)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editStudentFragment -> {
                student?.let {
                    val action = StudentDetailsFragmentDirections.actionStudentDetailsFragmentToEditStudentFragment(it)
                    Navigation.findNavController(requireView()).navigate(action)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
