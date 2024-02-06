package com.example.horizonhub_androidclient.data.gamePost


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_post_table")
data class GamePost(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val creator:String,
    val gameName: String,
    val gameImage: String,
    val description: String,
    val price: String,
)
