package com.example.horizonhub_androidclient.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.adapters.GamePostAdapter
import com.example.horizonhub_androidclient.data.gamePost.GamePost
import com.example.horizonhub_androidclient.data.gamePost.GamePostViewModel
import com.example.horizonhub_androidclient.databinding.FragmentAllPostsBinding
import com.example.horizonhub_androidclient.model.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    private var showMyPostsOnly = false

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
        try {
            fetchPostsFromFirebase()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerViewGamePosts
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        gamePostAdapter = GamePostAdapter(emptyList()) { gamePost ->
            database.child(gamePost.id).removeValue()
            gamePostAdapter.removeGamePost(gamePost)


        }
        binding.checkboxFilterMyPosts.setOnCheckedChangeListener { _, isChecked ->
            showMyPostsOnly = isChecked
            gamePostViewModel.allPosts.value?.let { gamePosts ->
                if (showMyPostsOnly) {
                    val currentUserPosts = gamePosts.filter { post -> post.creator == FirebaseAuth.getInstance().currentUser?.email }
                    gamePostAdapter.updateData(currentUserPosts)
                } else {
                    gamePostAdapter.updateData(gamePosts)
                }
            }
        }
        recyclerView.adapter = gamePostAdapter
        gamePostViewModel.allPosts.observe(viewLifecycleOwner) { gamePosts ->
            gamePosts?.let {
                if (showMyPostsOnly) {
                    val currentUserPosts = it.filter { post -> post.creator == FirebaseAuth.getInstance().currentUser?.email }
                    gamePostAdapter.updateData(currentUserPosts)
                } else {
                    gamePostAdapter.updateData(it)
                }
            }
        }
    }

    private fun fetchPostsFromFirebase() {
        val fetchedPostIds = mutableListOf<String>()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val localPosts = gamePostViewModel.allPosts.value ?: emptyList()

                for (postSnapshot in snapshot.children) {
                    val postId = postSnapshot.key!!
                    fetchedPostIds.add(postId)

                    val existingPost = localPosts.find { it.id == postId }

                    if (existingPost == null) {
                        val postModelMap = postSnapshot.value as Map<*, *>
                        gamePostViewModel.getGamePostById(postId)
                            .observe(viewLifecycleOwner) { post ->
                                if (post == null) {
                                    val postModel = PostModel(
                                        creator = postModelMap["creator"] as String,
                                        gameName = postModelMap["gameName"] as String,
                                        gameImage = postModelMap["gameImage"] as String,
                                        description = postModelMap["description"] as String,
                                        price = postModelMap["price"] as Long
                                    )

                                    gamePostViewModel.viewModelScope.launch {
                                        val byteArray = downloadImageAsByteArray(postModel.gameImage)
                                        if (byteArray != null) {
                                            val gamePost =
                                                GamePost(
                                                    id = postId,
                                                    creator = postModel.creator,
                                                    gameName = postModel.gameName,
                                                    gameImage = byteArray,
                                                    description = postModel.description,
                                                    price = postModel.price
                                                )
                                            gamePostViewModel.addGamePostToLocalDatabase(gamePost)
                                        }
                                    }
                                }
                            }
                    }
                }

                val deletedPosts = localPosts.filter { localPost -> localPost.id !in fetchedPostIds }
                deletedPosts.forEach { deletedPost ->
                    lifecycleScope.launch {
                        gamePostViewModel.deleteGamePostFromLocalDatabase(deletedPost.id)
                        gamePostViewModel.allPosts.value?.let { gamePosts ->
                            gamePostAdapter.updateData(gamePosts)
                        }

                    }
                }

                binding.swipeRefreshLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
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
