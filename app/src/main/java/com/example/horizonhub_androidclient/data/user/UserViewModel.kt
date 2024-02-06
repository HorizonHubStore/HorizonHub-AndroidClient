package com.example.horizonhub_androidclient.data.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    val allUsers: LiveData<List<User>>

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        allUsers = repository.getAllUsers()
    }

    fun addUserToLocalDatabase(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

    fun getUserById(userEmail: String): LiveData<User?> {
        return repository.getUserById(userEmail)
    }
    suspend fun updateProfileImage(userId: String, newProfileImage: String) {
        repository.updateProfileImage(userId, newProfileImage)
    }
}
