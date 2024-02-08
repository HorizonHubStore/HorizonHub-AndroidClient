package com.example.horizonhub_androidclient.data.gamePost

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamePostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GamePostRepository

    val allPosts: LiveData<List<GamePost>>

    init {
        val gamePostDao = GamePostDatabase.getDatabase(application).GamePostDao()
        repository = GamePostRepository(gamePostDao)
        allPosts = repository.getAllGamesPosts()
    }

    fun addGamePostToLocalDatabase(gamePost: GamePost) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addGamePost(gamePost)
        }
    }

    fun getGamePostById(gamePostId: String): LiveData<GamePost?> {
        return repository.getGamePostById(gamePostId)
    }
    suspend fun updateGamePostImage(gamePostId: String, newProfileImage: String) {
        repository.updateGamePostImage(gamePostId, newProfileImage)
    }
    fun getPostsByUser(userId: String): LiveData<List<GamePost>> {
        return repository.getPostsByUser(userId)
    }
}
