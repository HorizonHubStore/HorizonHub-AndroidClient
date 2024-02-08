package com.example.horizonhub_androidclient.data.gamePost


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_post_table")
data class GamePost(
    @PrimaryKey
    val id: String,
    val creator:String,
    val gameName: String,
    val gameImage: ByteArray?,
    val description: String,
    val price: Long,
)
