package com.example.horizonhub_androidclient.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.adapters.GamePostAdapter
import com.example.horizonhub_androidclient.data.gamePost.GamePostViewModel

class AllPostsFragment : Fragment(R.layout.fragment_all_posts) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gamePostAdapter: GamePostAdapter
    private lateinit var gamePostViewModel: GamePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_posts, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewGamePosts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        gamePostAdapter = GamePostAdapter(emptyList()) // Initialize with an empty list
        recyclerView.adapter = gamePostAdapter

        // Initialize the ViewModel
        gamePostViewModel = ViewModelProvider(this).get(GamePostViewModel::class.java)

        // Observe changes in allPosts LiveData
        gamePostViewModel.allPosts.observe(viewLifecycleOwner) { gamePosts ->
            // Update the RecyclerView adapter with the new list of game posts
            gamePostAdapter.updateData(gamePosts)
        }

        return view
    }
}

