package com.idz.ChallengeZone.model.dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.idz.ChallengeZone.base.MyApplication
import com.idz.ChallengeZone.model.Post
import com.idz.ChallengeZone.model.User

//@Database(entities = [Student::class], version = 1)
@Database(entities = [Post::class, User::class], version = 6)

abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
}
//class AppLocalDb {
object AppLocalDb {

    val database: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.context ?: throw IllegalStateException("Application context is missing")
        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}