// UserRepository.kt
package com.example.horizonhub_androidclient.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }

    fun getUserById(userEmail: String): LiveData<User?> {
        return userDao.getUserById(userEmail)
    }

    suspend fun updateProfileImage(userId: String, newProfileImage: String) {
        userDao.updateProfileImage(userId, newProfileImage)
    }
}
