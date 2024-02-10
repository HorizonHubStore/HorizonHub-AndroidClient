package com.example.horizonhub_androidclient.data.gamePost

import androidx.lifecycle.LiveData

class GamePostRepository(private val gamePostDao: GamePostDao) {

    suspend fun addGamePost(gamePost: GamePost) {
        gamePostDao.addGamePost(gamePost)
    }

    fun getAllGamesPosts(): LiveData<List<GamePost>> {
        return gamePostDao.getAllGamesPosts()
    }

    suspend fun deleteGamePostFromLocalDatabase(gamePostId: String) {
        gamePostDao.deleteGamePost(gamePostId)
    }

    fun getGamePostById(gamePostId: String): LiveData<GamePost?> {
        return gamePostDao.getGamePostById(gamePostId)
    }

    suspend fun updateGamePostImage(gamePostId: String, newProfileImage: String) {
        gamePostDao.updateGamePostImage(gamePostId, newProfileImage)
    }

    fun getPostsByUser(userId: String): LiveData<List<GamePost>> {
        return gamePostDao.getPostsByUser(userId)
    }
}
