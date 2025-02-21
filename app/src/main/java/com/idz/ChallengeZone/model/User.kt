package com.idz.ChallengeZone.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.idz.ChallengeZone.base.MyApplication

@Entity
data class User(
    @PrimaryKey val userName: String,
    val password: String,
    var avatarUrl: String,
    var email: String,
    val lastUpdated: Long? = null

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(password)
        parcel.writeString(avatarUrl)
        parcel.writeString(email)
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
        const val USER_NAME_KEY = "userName"
        const val PASSWORD_KEY = "password"
        const val AVATAR_URL_KEY = "avatarUrl"
        const val EMAIL_KEY = "email"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED_KEY = "localUserLastUpdated"
        // Create a Student instance from a Map
        fun fromJSON(json: Map<String, Any>): User {
            val userName = json[USER_NAME_KEY] as? String ?: ""
            val password = json[PASSWORD_KEY] as? String ?: ""
            val avatarUrl = json[AVATAR_URL_KEY] as? String ?: ""
            val email = json[EMAIL_KEY] as? String ?: ""
            val timestamp = json[LAST_UPDATED] as? Timestamp
            val longTimestamp = timestamp?.toDate()?.time
            // Creating the User object
            return User(
                userName = userName,
                password = password,
                avatarUrl = avatarUrl,  // You may want to change how avatarUrl is used
                email = email,  // Assuming address is empty in this case
                lastUpdated = longTimestamp
            )
        }

        // Parcelable.Creator implementation
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel): User {
                return User(parcel)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }
    }

    // Convert the User object to a Map
    val json: Map<String, Any>
        get() = hashMapOf(
            USER_NAME_KEY to userName,
            PASSWORD_KEY to password,
            AVATAR_URL_KEY to avatarUrl,
            EMAIL_KEY to email,
            LAST_UPDATED to FieldValue.serverTimestamp()
        )
}
