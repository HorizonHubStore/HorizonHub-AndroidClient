package com.example.horizonhub_androidclient.data.gamePost


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query



@Dao
interface GamePostDao {
    @Insert
    suspend fun addGamePost(gamePost: GamePost)
    @Query("DELETE FROM game_post_table WHERE id = :postId")
    suspend fun deleteGamePost(postId: String)
    @Query("SELECT * FROM game_post_table")
    fun getAllGamesPosts(): LiveData<List<GamePost>>


    @Query("SELECT * FROM game_post_table WHERE id = :gamePostId")
    fun getGamePostById(gamePostId: String): LiveData<GamePost?>

    @Query("UPDATE game_post_table SET gameImage = :newGamePostImage WHERE id = :gamePostId")
    suspend fun updateGamePostImage(gamePostId: String, newGamePostImage: String)

    @Query("SELECT * FROM game_post_table WHERE creator = :userId")
    fun getPostsByUser(userId: String): LiveData<List<GamePost>>
}
