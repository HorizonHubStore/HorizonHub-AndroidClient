package com.example.horizonhub_androidclient.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.adapters.GamePostAdapter
import com.example.horizonhub_androidclient.data.gamePost.GamePost
import com.example.horizonhub_androidclient.data.gamePost.GamePostViewModel
import com.example.horizonhub_androidclient.databinding.FragmentAllPostsBinding
import com.example.horizonhub_androidclient.model.PostModel
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AllPostsFragment : Fragment(R.layout.fragment_all_posts) {
    private lateinit var binding: FragmentAllPostsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var gamePostAdapter: GamePostAdapter
    private lateinit var gamePostViewModel: GamePostViewModel
    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllPostsBinding.inflate(inflater, container, false)
        gamePostViewModel = ViewModelProvider(this).get(GamePostViewModel::class.java)
        database = FirebaseDatabase.getInstance().getReference("gamePosts")

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchPostsFromFirebase()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerViewGamePosts
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        gamePostAdapter = GamePostAdapter(emptyList())
        recyclerView.adapter = gamePostAdapter

        gamePostViewModel.allPosts.observe(viewLifecycleOwner) { gamePosts ->
            gamePostAdapter.updateData(gamePosts)
        }
    }

    private fun fetchPostsFromFirebase() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val postId = postSnapshot.key
                    val postModelMap = postSnapshot.value as Map<String, Any>
                    val postModel = PostModel(
                        creator = postModelMap["creator"] as String,
                        gameName = postModelMap["gameName"] as String,
                        gameImage = postModelMap["gameImage"] as String,
                        description = postModelMap["description"] as String,
                        price = postModelMap["price"] as Long
                    )

                    postModel?.let {
                        gamePostViewModel.viewModelScope.launch {
                            val byteArray = downloadImageAsByteArray(it.gameImage)
                            if (byteArray != null) {
                                val gamePost = postId?.let { it1 ->
                                    GamePost(
                                        id = it1,
                                        creator = it.creator,
                                        gameName = it.gameName,
                                        gameImage = byteArray,
                                        description = it.description,
                                        price = it.price
                                    )
                                }
                                if (postId != null) {
                                    gamePostViewModel.getGamePostById(postId)
                                        .observe(viewLifecycleOwner) { post ->
                                            if (post == null && gamePost != null) {
                                                gamePostViewModel.addGamePostToLocalDatabase(gamePost)
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private suspend fun downloadImageAsByteArray(imageUrl: String): ByteArray? {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val byteArrayOutputStream = ByteArrayOutputStream()
                inputStream.use {
                    it.copyTo(byteArrayOutputStream)
                }
                byteArrayOutputStream.toByteArray()
            }
        } catch (e: IOException) {
            null
        }
    }
}
