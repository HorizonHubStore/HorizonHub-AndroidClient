package com.example.horizonhub_androidclient.data.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query



@Dao
interface UserDao {
    @Insert
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<User>>


    @Query("SELECT * FROM user_table WHERE id = :userId")
    fun getUserById(userId: String): LiveData<User?>

    @Query("UPDATE user_table SET profileImage = :newProfileImage WHERE id = :userId")
    suspend fun updateProfileImage(userId: String, newProfileImage: String)
}
