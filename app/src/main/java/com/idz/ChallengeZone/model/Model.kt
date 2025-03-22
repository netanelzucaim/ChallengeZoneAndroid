package com.idz.ChallengeZone.model
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.InvalidationTracker
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
    val posts: LiveData<List<Post>> = database.postDao().getAllPosts()

    val users: LiveData<List<User>> = database.userDao().getAllUsers()

    val loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()

    private val cloudinaryModel = CloudinaryModel()
    private val firebaseModel = FirebaseModel()

    init {
        // Listen for post deletions and update the local database
        firebaseModel.listenForPostDeletions { deletedPosts ->
            executor.execute {
                for (post in deletedPosts) {
                    database.postDao().delete(post)
                }
                mainHandler.post {
                    refreshAllPosts()
                }
            }
        }
    }

    companion object {
        val shared = Model()
    }


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
    fun refreshAllPostsOfLoggedUser() {
        Log.d("TAG", "refreshAllPostsOfLoggedUser")
        loadingState.postValue(LoadingState.LOADING)
        val lastUpdated: Long = Post.lastUpdated
        firebaseModel.getAllPostsOfLoggedUser(lastUpdated) { list ->
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
                postsOfLoggedUser.postValue(list) // Update postsOfLoggedUser LiveData
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


    fun deletePost(post: Post, callback: EmptyCallback) {
        firebaseModel.deletePost(post, callback)
        executor.execute {
            database.postDao().delete(post)
            mainHandler.post {
                refreshAllPosts()
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


    var id: String? = ""
    val postsOfLoggedUser: MutableLiveData<List<Post>> = MutableLiveData()

    fun getLoggedUser(lifecycleOwner: LifecycleOwner): LiveData<User?> {
        Log.d("TAG", "loggedUser")
        val id: String? = firebaseModel.getLoggedUserId()
        if (id != null) {
            this.id = id

            val userPosts = database.postDao().getAllPostsBySender(this.id!!)
            userPosts.observe(lifecycleOwner, Observer { posts ->
                postsOfLoggedUser.postValue(posts)
            })
        }
        val result = MutableLiveData<User?>()
        id?.let {
            database.userDao().getUserById(it).observe(lifecycleOwner) { otherUserValue ->
                mainHandler.post {
                    result.postValue(otherUserValue)
                }
            }
        } ?: result.postValue(null)
        return result
    }

    var otherUser: MutableLiveData<User?> = MutableLiveData()
    fun getOtherUser(lifecycleOwner: LifecycleOwner, id: String?): LiveData<User?> {
        val result = MutableLiveData<User?>()
        id?.let {
            database.userDao().getUserById(it).observe(lifecycleOwner) { otherUserValue ->
                mainHandler.post {
                    result.postValue(otherUserValue)
                }
            }
        } ?: result.postValue(null)
        return result
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }



}





