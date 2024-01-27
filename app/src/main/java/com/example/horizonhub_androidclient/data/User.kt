package com.example.horizonhub_androidclient.data

class User(val fullName: String, email: String, imagePath: String= "") {
    constructor(): this("", "", "")
}