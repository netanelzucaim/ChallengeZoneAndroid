package com.idz.ChallengeZone.model

import android.graphics.Bitmap
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.idz.ChallengeZone.base.Constants
import com.idz.ChallengeZone.base.EmptyCallback
import com.idz.ChallengeZone.base.StudentsCallback
import com.idz.ChallengeZone.model.Student
import com.idz.ChallengeZone.utils.extensions.toFirebaseTimestamp
import java.io.ByteArrayOutputStream
import java.util.Date
import android.util.Log

class FirebaseModel {

    private val database = Firebase.firestore
    private val storage = Firebase.storage
    init {

        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
        database.firestoreSettings = settings
    }

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
}