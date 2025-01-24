package com.idz.colman24class2.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.idz.colman24class2.base.MyApplication

@Entity
data class Student(
    @PrimaryKey val id: String,
    val name: String,
    var phone: String,
    var address: String,
    var isChecked: Boolean,
    val avatarUrl: String,
    val lastUpdated: Long? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(address)
        parcel.writeByte(if (isChecked) 1 else 0)
        parcel.writeString(avatarUrl)
        parcel.writeLong(lastUpdated ?: 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        var lastUpdated: Long
            get() = MyApplication.Globals.context?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                ?.getLong(LOCAL_LAST_UPDATED_KEY, 0) ?: 0

            set(value) {
                MyApplication.Globals.context
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.apply {
                        edit().putLong(LOCAL_LAST_UPDATED_KEY, value).apply()
                    }
            }
        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val PHONE_KEY = "phone"
        const val ADDRESS_KEY = "address"
        const val IS_CHECKED_KEY = "isChecked"
        const val AVATAR_URL_KEY = "avatarUrl"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED_KEY = "localStudentLastUpdated"
        // Create a Student instance from a Map
        fun fromJSON(json: Map<String, Any>): Student {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val phone = json[PHONE_KEY] as? String ?: ""
            val address = json[ADDRESS_KEY] as? String ?: ""
            val isChecked = json[IS_CHECKED_KEY] as? Boolean ?: false
            val avatarUrl = json[AVATAR_URL_KEY] as? String ?: ""
            val timestamp = json[LAST_UPDATED] as? Timestamp
            val longTimestamp = timestamp?.toDate()?.time
            // Creating the Student object
            return Student(
                id = id,
                name = name,
                phone = phone,  // You may want to change how avatarUrl is used
                address = address,  // Assuming address is empty in this case
                isChecked = isChecked,
                avatarUrl = avatarUrl,
                lastUpdated = longTimestamp
            )
        }

        // Parcelable.Creator implementation
        @JvmField
        val CREATOR: Parcelable.Creator<Student> = object : Parcelable.Creator<Student> {
            override fun createFromParcel(parcel: Parcel): Student {
                return Student(parcel)
            }

            override fun newArray(size: Int): Array<Student?> {
                return arrayOfNulls(size)
            }
        }
    }

    // Convert the Student object to a Map
    val json: Map<String, Any>
        get() = hashMapOf(
            ID_KEY to id,
            NAME_KEY to name,
            PHONE_KEY to phone,
            ADDRESS_KEY to address,
            IS_CHECKED_KEY to isChecked,
            AVATAR_URL_KEY to avatarUrl,
            LAST_UPDATED to FieldValue.serverTimestamp()
        )
}
