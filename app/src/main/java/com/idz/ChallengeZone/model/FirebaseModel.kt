package com.idz.ChallengeZone.model

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.idz.ChallengeZone.base.Constants
import com.idz.ChallengeZone.base.EmptyCallback
import com.idz.ChallengeZone.base.PostsCallback
import com.idz.ChallengeZone.utils.extensions.toFirebaseTimestamp
import java.io.ByteArrayOutputStream

typealias PostsCallback = (List<Post>) -> Unit
typealias UsersCallback = (List<User>) -> Unit


class FirebaseModel {

    private val database = Firebase.firestore
    private val storage = Firebase.storage
    val auth = Firebase.auth
    init {

        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
        database.firestoreSettings = settings
    }

    fun getAllPosts(sinceLastUpdated: Long, callback: PostsCallback) {
        database.collection(Constants.Collections.POSTS)
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED,sinceLastUpdated.toFirebaseTimestamp)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            Log.d("TAG","the number of posts are changed to ${json.data}")
                            posts.add(Post.fromJSON(json.data))
                        }
                        Log.d("TAG", posts.size.toString())
                        callback(posts)
                    }
                    false -> callback(listOf())
                }
            }
//            .addOnFailureListener(OnFailureListener {
//                Log.d("TAG", it.toString() + it.message)
//            })
    }
    fun getAllPostsOfLoggedUser(sinceLastUpdated: Long, callback: PostsCallback) {
        database.collection(Constants.Collections.POSTS)
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED,sinceLastUpdated.toFirebaseTimestamp)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            Log.d("TAG","the number of posts are changed to ${json.data}")
                            posts.add(Post.fromJSON(json.data))
                        }
                        Log.d("TAG", posts.size.toString())
                        callback(posts)
                    }
                    false -> callback(listOf())
                }
            }
//            .addOnFailureListener(OnFailureListener {
//                Log.d("TAG", it.toString() + it.message)
//            })
    }

    fun getAllUsers(sinceLastUpdated: Long, callback: UsersCallback) {
        database.collection(Constants.Collections.USERS)
            .whereGreaterThanOrEqualTo(User.LAST_UPDATED,sinceLastUpdated.toFirebaseTimestamp)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val users: MutableList<User> = mutableListOf()
                        for (json in it.result) {
                            Log.d("TAG","the number of users are changed to ${json.data}")
                            Log.d("TAG","the user last updated is $sinceLastUpdated")
                            users.add(User.fromJSON(json.data))
                        }
                        Log.d("TAG", users.size.toString())
                        callback(users)
                    }
                    false -> callback(listOf())
                }
            }
            .addOnFailureListener(OnFailureListener {
                Log.d("TAG", it.toString() + it.message)
            })
    }

    fun addPost(post: Post, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS).document(post.id).set(post.json)
            .addOnCompleteListener {
                callback()
            }
            .addOnFailureListener {
                Log.d("TAG", it.toString() + it.message)
            }
    }

//    fun add(student: Student, callback: EmptyCallback) {
//        database.collection(Constants.Collections.STUDENTS).document(student.id).set(student.json)
//            .addOnCompleteListener {
//                callback()
//            }
//            .addOnFailureListener {
//                Log.d("TAG", it.toString() + it.message)
//            }
//    }
    fun addUser(user: User, callback: EmptyCallback) {
        database.collection(Constants.Collections.USERS).document(user.id).set(user.json)
            .addOnCompleteListener {
                callback()
            }
            .addOnFailureListener {
                Log.d("TAG", it.toString() + it.message)
            }
    }

//    fun delete(student: Student, callback: EmptyCallback) {
//        database.collection(Constants.Collections.STUDENTS).document(student.id).delete()
//            .addOnCompleteListener {
//                callback()
//            }
//    }
//
//    fun update(student: Student, callback: EmptyCallback) {
//        database.collection(Constants.Collections.STUDENTS).document(student.id).set(student.json)
//            .addOnCompleteListener {
//                callback()
//            }
//    }
    fun deletePost(post: Post, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS).document(post.id).delete()
            .addOnCompleteListener {
                callback()
            }
    }

    fun updatePost(post: Post, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS).document(post.id).set(post.json)
            .addOnCompleteListener {
                callback()
            }
    }

    fun updateUser(user: User, callback: EmptyCallback) {
        database.collection(Constants.Collections.USERS).document(user.id).set(user.json)
            .addOnCompleteListener {
                callback()
            }
    }

    fun logOut() {
        auth.signOut()
    }


    fun uploadImage(image: Bitmap, name: String, callback: (String?) -> Unit) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/$name.jpg")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            callback(null)
        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }
        }
    }



    fun logIn(username: String, password: String, callback: (Boolean?) -> Unit) {
        database.collection(Constants.Collections.USERS).whereEqualTo("userName", username)
            .get()
            .addOnCompleteListener { task ->
                val result = task.result
                if (task.isSuccessful && result != null && !result.isEmpty) {
                    val document = result.documents[0]
                    val data = document.data
                    if (data != null) {
                        val user = User.fromJSON(data)
                        if (user != null) {
                            auth.signInWithEmailAndPassword(user.email, password)
                                .addOnCompleteListener { authTask ->
                                    val profile =
                                        UserProfileChangeRequest.Builder().setDisplayName(user.id)
                                            .build()
                                    auth.getCurrentUser()?.updateProfile(profile)
//                                    Model.shared.username = user.userName
                                    callback(authTask.isSuccessful)
                                }
                        } else {
                            callback(false)
                        }
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }
    }

    fun isUsernameTaken(username: String, callback: (Boolean?) -> Unit) {
        database.collection(Constants.Collections.USERS).document(username).get()
            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        callback(document.exists())
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            })
    }

    fun isEmailTaken(email: String, callback: (Boolean?) -> Unit) {
        database.collection(Constants.Collections.USERS)
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    callback(!documents.isEmpty)
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun signUp(newUser: User, password: String, callback: EmptyCallback) {

        database.collection(Constants.Collections.USERS).document(newUser.id).set(newUser.json)
            .addOnCompleteListener(OnCompleteListener<Void?> {
                auth.createUserWithEmailAndPassword(newUser.email, password)
                    .addOnCompleteListener(OnCompleteListener<AuthResult?> {
                         val profile =
                             UserProfileChangeRequest.Builder().setDisplayName(newUser.id)
                                .build()
                        auth.getCurrentUser()?.updateProfile(profile)
//                        Model.shared.username = newUser.userName
                        auth.signInWithEmailAndPassword(newUser.email, password)
                            .addOnCompleteListener(
                                OnCompleteListener<AuthResult?> {
                                    callback()
                                })
                    })
            })
            .addOnFailureListener( OnFailureListener {
                Log.d("TAG", it.toString() + it.message)
            })
    }


    fun getLoggedUserId(): String? {
        val id: String? = auth.currentUser?.displayName
        return id
    }
}