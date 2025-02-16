package com.idz.ChallengeZone.model

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.idz.ChallengeZone.base.Constants
import com.idz.ChallengeZone.base.EmptyCallback
import com.idz.ChallengeZone.base.StudentsCallback
import com.idz.ChallengeZone.utils.extensions.toFirebaseTimestamp
import java.io.ByteArrayOutputStream

class FirebaseModel {

    private val database = Firebase.firestore
    private val storage = Firebase.storage
    val auth = Firebase.auth
    init {

        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
        database.firestoreSettings = settings
//
//        auth.createUserWithEmailAndPassword("talzi@colman.ac.il", "supperStrong").addOnCompleteListener {
//            if(it.isSuccessful){
//                Log.i("TAG", "Successfully registered")
//
//            }else {
//                Log.i("TAG", auth.currentUser?.uid ?: "No use uuid")
//
//            }
//        }

//        //        auth.createUserWithEmailAndPassword("talzi@colman.ac.il", "supperStrong")
//        auth.currentUser?.uid?.let {
//            Log.i("TAG", auth.currentUser?.uid ?: "No use uuid")
//            val json = hashMapOf(
//                "name" to "Tal",
//                "email" to "talzi@colman.ac.il"
//            )
//            database.collection("users_nati").document(it).set(json)
//                .addOnCompleteListener {
//                    Log.i("TAG", auth.currentUser?.uid + "Saved" ?: "No use uuid")
//                }
//        }

    }

    /*
    val db = Firebase.firestore
    // Create a new user with a first and last name
    val user = hashMapOf(
        "first" to "Ada",
        "last" to "Lovelace",
        "born" to 1815,
    )
    // Add a new document with a generated ID
    db.collection("users")
    .add(user)
    .addOnSuccessListener { documentReference ->
        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
    }
    .addOnFailureListener { e ->
        Log.w("TAG", "Error adding document", e)
    }
     */
//    fun signUpUser( callback: EmptyCallback) {
//        // Create a new user with a first and last name
//        val user = hashMapOf(
//            "first" to "Ada",
//            "last" to "Lovelace",
//            "born" to 1815,
//        )
//        // Add a new document with a generated ID
//        database.collection("users")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w("TAG", "Error adding document", e)
//            }
//
//    }


        fun getAllStudents(sinceLastUpdated: Long, callback: StudentsCallback) {
        database.collection(Constants.Collections.STUDENTS)
            .whereGreaterThanOrEqualTo(Student.LAST_UPDATED,sinceLastUpdated.toFirebaseTimestamp)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val students: MutableList<Student> = mutableListOf()
                        for (json in it.result) {
                            students.add(Student.fromJSON(json.data))
                        }
                        Log.d("TAG", students.size.toString())
                        callback(students)
                    }
                    false -> callback(listOf())
            }
        }
    }

    fun add(student: Student, callback: EmptyCallback) {
        database.collection(Constants.Collections.STUDENTS).document(student.id).set(student.json)
            .addOnCompleteListener {
                callback()
            }
            .addOnFailureListener {
                Log.d("TAG", it.toString() + it.message)
            }
    }
    fun addUser(user: User, callback: EmptyCallback) {
        database.collection(Constants.Collections.USERS).document(user.userName).set(user.json)
            .addOnCompleteListener {
                callback()
            }
            .addOnFailureListener {
                Log.d("TAG", it.toString() + it.message)
            }
    }

    fun delete(student: Student, callback: EmptyCallback) {
        database.collection(Constants.Collections.STUDENTS).document(student.id).delete()
            .addOnCompleteListener {
                callback()
            }
    }

    fun update(student: Student, callback: EmptyCallback) {
        database.collection(Constants.Collections.STUDENTS).document(student.id).set(student.json)
            .addOnCompleteListener {
                callback()
            }
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
        database.collection(Constants.Collections.USERS).document(username)
            .get()
            .addOnCompleteListener { task ->
                val result = task.result
                if (task.isSuccessful && result != null) {
                    val data = result.data
                    if (data != null) {
                        val user = User.fromJSON(data)
                        if (user != null) {
                            auth.signInWithEmailAndPassword(user.email, password)
                                .addOnCompleteListener { authTask ->
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

        database.collection(Constants.Collections.USERS).document(newUser.userName).set(newUser.json)
            .addOnCompleteListener(OnCompleteListener<Void?> {
                auth.createUserWithEmailAndPassword(newUser.email, password)
                    .addOnCompleteListener(OnCompleteListener<AuthResult?> {
                       // val profile =
                       //     UserProfileChangeRequest.Builder().setDisplayName(newUser.userName)
                        //        .build()
                        //mAuth.getCurrentUser().updateProfile(profile)
                        //Model.getInstance().username = newUser.userName
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


}