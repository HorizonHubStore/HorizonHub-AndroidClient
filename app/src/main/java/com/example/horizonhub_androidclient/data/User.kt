package com.example.horizonhub_androidclient.data

data class User(val fullName: String, val email: String, val imagePath: String = "") {
    constructor() : this("", "", "")
}