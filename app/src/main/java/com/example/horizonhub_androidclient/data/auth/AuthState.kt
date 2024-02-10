package com.example.horizonhub_androidclient.data.auth

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_state")
data class AuthState(
    @PrimaryKey
    val id: Int = 1,
    val isLoggedIn: Boolean,
    val userId: String?
)
