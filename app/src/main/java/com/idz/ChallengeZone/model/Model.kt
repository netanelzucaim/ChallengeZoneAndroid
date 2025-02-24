package com.idz.ChallengeZone.model
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.idz.ChallengeZone.model.dao.AppLocalDb
import com.idz.ChallengeZone.model.dao.AppLocalDbRepository
import java.util.concurrent.Executors

typealias EmptyCallback = () -> Unit


class Model private constructor() {
    enum class LoadingState {
        LOADING,
        LOADED
    }

    enum class Storage {
        FIREBASE,
        CLOUDINARY
    }

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
//    val students: LiveData<List<Student>> = database.studentDao().getAllStudent()
    val posts: LiveData<List<Post>> = database.postDao().getAllPosts()
    val users: LiveData<List<User>> = database.userDao().getAllUsers()

    val loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()

    private val cloudinaryModel = CloudinaryModel()
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = Model()
    }
//
//    fun refreshAllStudents() {
//        loadingState.postValue(LoadingState.LOADING)
//        var lastUpdated: Long = Student.lastUpdated
//        firebaseModel.getAllStudents(lastUpdated) { list ->
//            executor.execute {
//                var currentTime = lastUpdated
//                for (student in list) {
//                    database.studentDao().insertStudents(student)
//                    student.lastUpdated?.let {
//                        if (currentTime < it) {
//                            currentTime = it
//
//                        }
//                    }
//                }
//                Student.lastUpdated = currentTime
//                loadingState.postValue(LoadingState.LOADED)
//            }
//
//        }
//    }

    fun refreshAllUsers() {
        Log.d("TAG", "refreshAllUsers")
        loadingState.postValue(LoadingState.LOADING)
        Log.d("TAG","user last updated is ${User.lastUpdated}")
        var lastUpdated: Long = User.lastUpdated
        firebaseModel.getAllUsers(lastUpdated) { list ->
            executor.execute {
                var currentTime = lastUpdated
                for (user in list) {
                    database.userDao().insertUsers(user)
                    user.lastUpdated?.let {
                        if (currentTime < it) {
                            currentTime = it

                        }
                    }
                }
                User.lastUpdated = currentTime
                loadingState.postValue(LoadingState.LOADED)
            }
        }
    }

//    fun add(student: Student, image: Bitmap?, storage: Storage, callback: EmptyCallback) {
//        firebaseModel.add(student) {
//            image?.let {
//                uploadTo(
//                    storage,
//                    image = image,
//                    name = student.id,
//                    callback = { uri ->
//                        if (!uri.isNullOrBlank()) {
//                            val st = student.copy(avatarUrl = uri)
//                            firebaseModel.add(st, callback)
//                        } else {
//                            callback()
//                        }
//                    },
//                )
//            } ?: callback()
//        }
//    }

    fun refreshAllPosts() {
        //need to refresh all suer because a user can updated his details...
        refreshAllUsers()
        Log.d("TAG", "refreshAllPosts")
        loadingState.postValue(LoadingState.LOADING)
        var lastUpdated: Long = Post.lastUpdated
        firebaseModel.getAllPosts(lastUpdated) { list ->
            executor.execute {
                var currentTime = lastUpdated
                for (post in list) {
                    database.postDao().insertPosts(post)
                    post.lastUpdated?.let {
                        if (currentTime < it) {
                            currentTime = it
                        }
                    }
                }
                Post.lastUpdated = currentTime
                loadingState.postValue(LoadingState.LOADED)
            }
        }
    }


    fun updatePost(post: Post, image: Bitmap?,storage: Storage, callback: EmptyCallback) {
        firebaseModel.updatePost(post) {
            image?.let {
                uploadTo(
                    storage,
                    image = image,
                    name = post.id,
                    callback = { uri ->
                        if (!uri.isNullOrBlank()) {
                            val po = post.copy(postPic = uri)
                            firebaseModel.updatePost(po, callback)
                        } else {
                            callback()
                        }
                    },
                )
            } ?: callback()
        }
    }

    fun updateUser(user: User, image: Bitmap?,storage: Storage, callback: EmptyCallback) {
        firebaseModel.updateUser(user) {
            image?.let {
                uploadTo(
                    storage,
                    image = image,
                    name = user.id,
                    callback = { uri ->
                        if (!uri.isNullOrBlank()) {
                            val us = user.copy(avatarUrl = uri)
                            firebaseModel.updateUser(us, callback)
                        } else {
                            callback()
                        }
                    },
                )
            } ?: callback()
        }
    }

    fun addPost(post: Post, image: Bitmap?, storage: Storage, callback: EmptyCallback) {
        firebaseModel.addPost(post) {
            image?.let {
                uploadTo(
                    storage,
                    image = image,
                    name = post.id,
                    callback = { uri ->
                        if (!uri.isNullOrBlank()) {
                            val po = post.copy(postPic = uri)
                            firebaseModel.addPost(po, callback)
                        } else {
                            callback()
                        }
                    },
                )
            } ?: callback()
        }
    }

    private fun uploadTo(
        storage: Storage,
        image: Bitmap,
        name: String,
        callback: (String?) -> Unit
    ) {
        when (storage) {
            Storage.FIREBASE -> {
                uploadImageToFirebase(image, name, callback)
            }

            Storage.CLOUDINARY -> {
                uploadImageToCloudinary(
                    bitmap = image,
                    name = name,
                    onSuccess = callback,
                    onError = { callback(null) }
                )
            }
        }
    }

    //
    fun signUp(
        user: User,
        password: String,
        image: Bitmap?,
        storage: Storage,
        callback: EmptyCallback
    ) {
        firebaseModel.signUp(user, password) {
            image?.let {
                uploadTo(
                    storage,
                    image = image,
                    name = user.userName,
                    callback = { uri ->
                        if (!uri.isNullOrBlank()) {
                            val us = user.copy(avatarUrl = uri)
                            firebaseModel.addUser(us, callback)
                        } else {
                            callback()
                        }
                    },
                )
            } ?: callback()
        }
        refreshAllUsers()
    }

    fun logIn(username: String, password: String, callback: (Boolean?) -> Unit) {
        firebaseModel.logIn(username, password) { isSuccessful ->
            callback(isSuccessful)
        }
    }

    fun isUserNameTaken(username: String, callback: (Boolean) -> Unit) {
        firebaseModel.isUsernameTaken(username) { isTaken ->
            callback(isTaken ?: false)
        }
    }

    fun isEmailTaken(email: String, callback: (Boolean) -> Unit) {
        firebaseModel.isEmailTaken(email) { isTaken ->
            callback(isTaken ?: false)
        }
    }
//
//    fun delete(student: Student, callback: EmptyCallback) {
//        firebaseModel.delete(student, callback)
//    }
//
//    fun update(student: Student, callback: EmptyCallback) {
//        firebaseModel.update(student, callback)
//    }

    fun deletePost(post: Post, callback: EmptyCallback) {
        firebaseModel.deletePost(post, callback)
        executor.execute {
            database.postDao().delete(post)
            mainHandler.post {
                callback()
            }
        }
    }
    fun logOut() {
        firebaseModel.logOut()
    }


    private fun uploadImageToFirebase(
        image: Bitmap,
        name: String,
        callback: (String?) -> Unit
    ) {
        firebaseModel.uploadImage(image, name, callback)
    }

    private fun uploadImageToCloudinary(
        bitmap: Bitmap,
        name: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        cloudinaryModel.uploadImage(
            bitmap = bitmap,
            name = name,
            onSuccess = onSuccess,
            onError = onError
        )
    }

//    private var user: MutableLiveData<User?> = MutableLiveData()
//    var username: String? = null
//    fun getLoggedUser(): LiveData<User?> {
//        val username: String? = firebaseModel.getLoggedUserUsername()
//        if (username != null) {
//            this.username = username
//            Log.d("TAG", "loggedUser is $username")
//
//        }
//        if (user.value == null) {
//            this.username?.let {
//                user.postValue(database.userDao().getUserByUsername(it).value)
//                refreshAllUsers()
//            }
//        }
//        return user
//    }


    var username: String? = null
    fun getLoggedUser(): LiveData<User?> {
        Log.d("TAG", "loggedUser")
        val username: String? = firebaseModel.getLoggedUserUsername()
        if (username != null) {
            this.username = username
        }
        val result = MutableLiveData<User?>()
        username?.let {
            database.userDao().getUserByUsername(it).observeForever { otherUserValue ->
                Log.d("TAG", "get logged username of $username")
                Log.d("TAG", "logged username is $username and its details are $otherUserValue")
                mainHandler.post {
                    result.postValue(otherUserValue)
                }
            }
        } ?: result.postValue(null)
        return result
    }

    var otherUser: MutableLiveData<User?> = MutableLiveData()
    fun getOtherUser(username: String?): LiveData<User?> {
        val result = MutableLiveData<User?>()
        username?.let {
            database.userDao().getUserByUsername(it).observeForever { otherUserValue ->
//                Log.d("TAG", "get username of $username")
//                Log.d("TAG", "otherUsername is $username and its details are $otherUserValue")
                mainHandler.post {
                    result.postValue(otherUserValue)
                }
            }
        } ?: result.postValue(null)
        return result
    }




}





