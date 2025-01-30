package com.idz.ChallengeZone.model
import android.graphics.Bitmap
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.google.android.gms.auth.api.signin.internal.Storage
import com.idz.ChallengeZone.model.CloudinaryModel
import com.idz.ChallengeZone.model.dao.AppLocalDb
import com.idz.ChallengeZone.model.dao.AppLocalDbRepository
import java.util.concurrent.Executors
typealias StudentsCallback = (List<Student>) -> Unit
typealias EmptyCallback = () -> Unit
interface GetAllStudentsListener {
    fun onCompletion(students: List<Student>)
}
class Model private constructor() {
    enum class Storage {
        FIREBASE,
        CLOUDINARY
    }
    // The list of students is now of type ArrayList<Student>, where Student is Parcelable
//    val students: MutableList<Student> = ArrayList()

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    private val cloudinaryModel = CloudinaryModel()
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = Model()
    }

    fun getAllStudents(callback: StudentsCallback) {
//        firebaseModel.getAllStudents(callback)
        var lastUpdated: Long = Student.lastUpdated
        firebaseModel.getAllStudents(lastUpdated){ list ->
            executor.execute{
                var currentTime = lastUpdated
                for(student in list){
                    database.studentDao().insertStudents(student)
                    student.lastUpdated?.let {
                        if (currentTime < it) {
                            currentTime = it

                        }
                    }
                }
                Student.lastUpdated = currentTime
                val students = database.studentDao().getAllStudents()
                mainHandler.post {
                    callback(students)
                }
            }

        }
    }



    fun add(student: Student, image: Bitmap?, storage: Storage, callback: EmptyCallback) {
        firebaseModel.add(student) {
            image?.let {
                uploadTo(
                    storage,
                    image = image,
                    name = student.id,
                    callback = { uri ->
                        if (!uri.isNullOrBlank()) {
                            val st = student.copy(avatarUrl = uri)
                            firebaseModel.add(st, callback)
                        } else {
                            callback()
                        }
                    },
                )
            } ?: callback()
        }
    }

    private fun uploadTo(storage: Storage, image: Bitmap, name: String, callback: (String?) -> Unit) {
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

    fun delete(student: Student, callback: EmptyCallback) {
        firebaseModel.delete(student, callback)
    }

    fun update(student: Student, callback: EmptyCallback) {
        firebaseModel.update(student, callback)
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
}





