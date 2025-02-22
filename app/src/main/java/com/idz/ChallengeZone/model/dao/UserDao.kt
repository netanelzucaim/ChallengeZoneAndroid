package com.idz.ChallengeZone.model.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.lifecycle.LiveData

import com.idz.ChallengeZone.model.User
@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAllUsers(): LiveData<List<User>>
    @Query("SELECT * FROM User WHERE userName =:userName")
    fun getUserByUsername(userName: String): LiveData<User>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: User)
    @Delete
    fun delete(user: User)
    @Update
    fun updateUser(user: User)
}