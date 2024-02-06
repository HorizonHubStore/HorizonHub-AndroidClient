package com.example.horizonhub_androidclient.data.gamePost

import androidx.lifecycle.LiveData

class GamePostRepository(private val gamePostDao: GamePostDao) {

    suspend fun addGamePost(gamePost: GamePost) {
        gamePostDao.addGamePost(gamePost)
    }

    fun getAllGamesPosts(): LiveData<List<GamePost>> {
        return gamePostDao.getAllGamesPosts()
    }

    fun getGamePostById(gamePostId: String): LiveData<GamePost?> {
        return gamePostDao.getGamePostById(gamePostId)
    }

    suspend fun updateGamePostImage(gamePostId: String, newProfileImage: String) {
        gamePostDao.updateGamePostImage(gamePostId, newProfileImage)
    }
}
