package com.example.horizonhub_androidclient.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.horizonhub_androidclient.data.gamePost.GamePost
import com.example.horizonhub_androidclient.data.gamePost.GamePostViewModel
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SharedGamePostViewModel(application: Application) : AndroidViewModel(application) {
    private val gamePostViewModel = GamePostViewModel(application)

    val allPosts: LiveData<List<GamePost>> = gamePostViewModel.allPosts

    fun fetchPostsFromFirebase() {
        viewModelScope.launch {
            // Your code to fetch posts from Firebase
        }
    }
}
