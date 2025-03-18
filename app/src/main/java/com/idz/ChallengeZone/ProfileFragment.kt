package com.idz.ChallengeZone

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.ChallengeZone.adapter.postAdapter.PostsRecyclerAdapter
import com.idz.ChallengeZone.databinding.FragmentProfileBinding
import com.idz.ChallengeZone.model.User
import com.idz.ChallengeZone.viewmodel.AuthViewModel
import com.idz.ChallengeZone.viewmodel.PostViewModel
import com.idz.ChallengeZone.viewmodel.UserViewModel
import com.idz.ChallengeZone.model.Model
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private val postsViewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private var adapter: PostsRecyclerAdapter? = null
    private var binding: FragmentProfileBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false
    private var isEditing = false  // משתנה שמייצג אם אנחנו במצב עריכה
    var user: User? = null

    // משתנים לשמירת הערכים המקוריים
    private var originalUserName: String? = null
    private var originalAvatarUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // כפתור ה-Edit/Save
        binding?.saveButton?.setOnClickListener {
            if (isEditing) {
                onSaveClicked(it)  // אם אנחנו במצב עריכה - שמור את השינויים
            } else {
                enableEditing()  // אם אנחנו במצב צפייה - הפוך לעריכה
            }
        }

        // כפתור ה-Logout/Cancel
        binding?.logoutButton?.setOnClickListener {
            if (isEditing) {
                cancelEditing()  // אם אנחנו במצב עריכה - בצע ביטול
            } else {
                onLogoutClicked(it)  // אם אנחנו במצב צפייה - בצע התנתקות
            }
        }

        // הפעלת מצלמה
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.avatarImageView?.setImageBitmap(bitmap)
            didSetProfileImage = true
        }

        binding?.takePhotoButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        setupView()
        setupRecyclerView()

        return binding?.root
    }

    private fun setupRecyclerView() {
        binding?.recyclerView?.setHasFixedSize(true)
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(postsViewModel.postsOfLoggedUser.value)
        postsViewModel.postsOfLoggedUser.observe(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
        binding?.swipeToRefresh?.setOnRefreshListener(postsViewModel::refreshAllPosts)
        binding?.recyclerView?.adapter = adapter
        disableEditing()  // ברגע שהרשימה נטענת, המצב יהיה read-only
    }

    private fun setupView() {
        userViewModel.fetchUser().observe(viewLifecycleOwner) { loggedUser ->
            user = loggedUser
            user?.userName?.let { userName ->
                binding?.userNameEditText?.setText(userName)
            }
            user?.avatarUrl?.let { avatarUrl ->
                if (avatarUrl.isNotBlank()) {
                    Picasso.get()
                        .load(avatarUrl)
                        .placeholder(R.drawable.avatar)
                        .into(binding?.avatarImageView)
                }
            }

            // שמור את הערכים המקוריים לפני המעבר לעריכה
            originalUserName = user?.userName
            originalAvatarUrl = user?.avatarUrl

            // הפוך את השדות למצב read-only ברגע שהמשתמש טוען את המידע
            disableEditing()
        }
    }

    private fun enableEditing() {
        // שמור את הערכים המקוריים לפני שינוי
        originalUserName = binding?.userNameEditText?.text.toString()
        originalAvatarUrl = user?.avatarUrl

        // הפוך את השדות לעריכה
        binding?.userNameEditText?.isEnabled = true
        binding?.takePhotoButton?.isEnabled = true
        binding?.saveButton?.text = "Save" // שינוי הכפתור ל-Save
        isEditing = true
        updateLogoutButton() // עדכון כפתור ה-logout ל-Cancel במצב עריכה
    }

    private fun disableEditing() {
        // הפוך את השדות ללא ניתנים לעריכה
        binding?.userNameEditText?.isEnabled = false
        binding?.takePhotoButton?.isEnabled = false
        binding?.saveButton?.text = "Edit" // שינוי הכפתור חזרה ל-Edit
        isEditing = false
        updateLogoutButton() // עדכון כפתור ה-logout ל-Logout במצב צפייה
    }

    private fun updateLogoutButton() {
        if (isEditing) {
            binding?.logoutButton?.text = "Cancel" // שינוי ל-Cancel במצב עריכה
        } else {
            binding?.logoutButton?.text = "Logout" // החזר ל-Logout במצב צפייה
        }
    }

    private fun cancelEditing() {
        // החזר את הערכים המקוריים למצבם הקודם
        binding?.userNameEditText?.setText(originalUserName)
        user?.avatarUrl = originalAvatarUrl.toString()
        Picasso.get()
            .load(user?.avatarUrl)
            .placeholder(R.drawable.avatar)
            .into(binding?.avatarImageView)

        // החזר את המצב לקריאה בלבד
        disableEditing()
    }

    private fun onSaveClicked(view: View) {
        // אם אנחנו במצב עריכה, נבצע שמירה
        binding?.progressBar?.visibility = View.VISIBLE
        authViewModel.checkUsernameTaken(binding?.userNameEditText?.text.toString())

        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.isUsernameTaken.observe(viewLifecycleOwner) { isTaken ->
            if (isTaken == true && binding?.userNameEditText?.text.toString() != user?.userName) {
                binding?.progressBar?.visibility = View.GONE
                makeAToast("Username already been taken")
            } else {
                updateUser()  // אם לא נמצא שם משתמש כפול, נעדכן את המשתמש
            }
        }
    }

    private fun updateUser() {
        setUser()
        if (didSetProfileImage) {
            binding?.avatarImageView?.isDrawingCacheEnabled = true
            binding?.avatarImageView?.buildDrawingCache()
            val bitmap = (binding?.avatarImageView?.drawable as BitmapDrawable).bitmap

            userViewModel.updateUser(user!!, bitmap, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                getAllPosts()  // Refresh the posts list
                disableEditing()  // החזר את המצב ל-read only
            }
        } else {
            userViewModel.updateUser(user!!, null, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                getAllPosts()  // Refresh the posts list
                disableEditing()  // החזר את המצב ל-read only
            }
        }
    }

    private fun onLogoutClicked(view: View) {
        authViewModel.logOut()
        val navController = Navigation.findNavController(view)
        val action = ProfileFragmentDirections.actionGlobalSignInFragment()
        navController.navigate(action)
    }

    override fun onResume() {
        super.onResume()
        getAllPosts()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getAllPosts() {
        binding?.progressBar?.visibility = View.VISIBLE
        postsViewModel.refreshAllPosts()
    }

    private fun setUser() {
        user = user?.copy(
            id = user?.id ?: "",
            userName = binding?.userNameEditText?.text.toString(),
            password = user?.password ?: "",
            avatarUrl = user?.avatarUrl ?: "",
            email = user?.email ?: ""
        )
    }

    private fun makeAToast(text: String?) {
        AlertDialog.Builder(context)
            .setTitle("Notification")
            .setMessage(text)
            .create().show()
    }
}
