package com.example.horizonhub_androidclient.data.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.horizonhub_androidclient.data.auth.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    val allUsers: LiveData<List<User>>

    // Add LiveData for AuthStateEntity
    val dbAuthState: LiveData<AuthState?>

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        val authStateDao = UserDatabase.getDatabase(application).authStateDao()
        repository = UserRepository(userDao, authStateDao)
        allUsers = repository.getAllUsers()
        dbAuthState = repository.getAuthState()
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

    suspend fun updateAuthState(authStateEntity: AuthState) {
        repository.updateAuthState(authStateEntity)
    }

    fun getAuthState(): LiveData<AuthState?> {
        return repository.getAuthState()
    }
}
