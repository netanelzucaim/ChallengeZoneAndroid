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
data class Post(
    @PrimaryKey val id: String,
    val sender: String,
    var content: String,
    val postPic: String? = "",
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
        parcel.writeString(id)
        parcel.writeString(sender)
        parcel.writeString(content)
        parcel.writeString(postPic)
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
        const val SENDER_KEY = "sender"
        const val CONTENT_KEY = "content"
        const val POST_PIC_KEY = "postPic"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED_KEY = "localPostLastUpdated"
        // Create a Post instance from a Map
        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: ""
            val sender = json[SENDER_KEY] as? String ?: ""
            val content = json[CONTENT_KEY] as? String ?: ""
            val postPic = json[POST_PIC_KEY] as? String ?: ""
            val timestamp = json[LAST_UPDATED] as? Timestamp
            val longTimestamp = timestamp?.toDate()?.time
            // Creating the Post object
            return Post(
                id = id,
                sender = sender,
                content = content,
                postPic = postPic,
                lastUpdated = longTimestamp
            )
        }

        // Parcelable.Creator implementation
        @JvmField
        val CREATOR: Parcelable.Creator<Post> = object : Parcelable.Creator<Post> {
            override fun createFromParcel(parcel: Parcel): Post {
                return Post(parcel)
            }

            override fun newArray(size: Int): Array<Post?> {
                return arrayOfNulls(size)
            }
        }
    }

    // Convert the Post object to a Map
    val json: Map<String, Any>
        get() {
            val map = hashMapOf(
                ID_KEY to id,
                SENDER_KEY to sender,
                CONTENT_KEY to content,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
            postPic?.let { map[POST_PIC_KEY] = it }
            return map
        }
}