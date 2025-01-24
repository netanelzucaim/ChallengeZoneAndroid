package com.idz.colman24class2.model.dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.idz.colman24class2.base.MyApplication
import com.idz.colman24class2.model.Student
//@Database(entities = [Student::class], version = 1)
@Database(entities = [Student::class], version = 3)

abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun studentDao(): StudentDao
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