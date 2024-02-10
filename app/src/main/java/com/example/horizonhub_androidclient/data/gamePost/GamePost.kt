package com.example.horizonhub_androidclient.data.gamePost


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey
@Parcelize
@Entity(tableName = "game_post_table")
data class GamePost(
    @PrimaryKey
    val id: String,
    val creator:String,
    val gameName: String,
    val gameImage: ByteArray?,
    val description: String,
    val price: Long,
) : Parcelable