package com.example.horizonhub_androidclient.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val fullName: String,
    val profileImage: ByteArray,
    val lastUpdate : Long
)
