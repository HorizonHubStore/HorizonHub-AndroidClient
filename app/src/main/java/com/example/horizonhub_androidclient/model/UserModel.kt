package com.example.horizonhub_androidclient.model

import java.util.Date

data class UserModel(
    val id: String,
    val email: String,
    val fullName: String,
    val profileImage: String,
    val lastUpdate : Long
)
