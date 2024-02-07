package com.example.horizonhub_androidclient.data.user

import androidx.lifecycle.LiveData
import com.example.horizonhub_androidclient.data.auth.AuthState
import com.example.horizonhub_androidclient.data.auth.AuthStateDao

class UserRepository(private val userDao: UserDao,private val authStateDao: AuthStateDao) {

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

    suspend fun updateAuthState(authStateEntity: AuthState) {
        authStateDao.insertOrUpdateAuthState(authStateEntity)
    }

    fun getAuthState(): LiveData<AuthState?> {
        return authStateDao.getAuthState()
    }
}
