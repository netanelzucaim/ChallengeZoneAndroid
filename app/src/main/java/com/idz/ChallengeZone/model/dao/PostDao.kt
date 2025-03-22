package com.idz.ChallengeZone.model.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.lifecycle.LiveData

import com.idz.ChallengeZone.model.Post
@Dao
interface PostDao {
    @Query("SELECT * FROM Post")
    fun getAllPosts(): LiveData<List<Post>>
    @Query("SELECT * FROM Post WHERE sender =:sender")
    fun getAllPostsBySender(sender: String): LiveData<List<Post>>
    @Query("SELECT * FROM Post WHERE id =:id")
    fun getPostById(id: String): Post
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(vararg posts: Post)
    @Delete
    fun delete(post: Post)
    @Update
    fun updatePost(post: Post)
}