package com.example.horizonhub_androidclient.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.data.gamePost.GamePost
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class GamePostAdapter(
    private var gamePosts: List<GamePost>,
    private val onDeleteClick: (GamePost) -> Unit
) : RecyclerView.Adapter<GamePostAdapter.GamePostViewHolder>() {

    inner class GamePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCreator: TextView = itemView.findViewById(R.id.textViewCreator)
        val textViewGameName: TextView = itemView.findViewById(R.id.textViewGameName)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val imageViewGameImage: ImageView = itemView.findViewById(R.id.imageViewGameImage)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    fun updateData(newGamePosts: List<GamePost>) {
        gamePosts = newGamePosts
        notifyDataSetChanged()
    }

    fun removeGamePost(gamePost: GamePost) {
        val position = gamePosts.indexOf(gamePost)
        if (position != -1) {
            gamePosts = gamePosts.toMutableList().apply { removeAt(position) }
            notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamePostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_post_item, parent, false)
        return GamePostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GamePostViewHolder, position: Int) {
        val currentItem = gamePosts[position]
        holder.textViewCreator.text = "Creator: ${currentItem.creator}"
        holder.textViewGameName.text = "Game Name: ${currentItem.gameName}"
        holder.textViewDescription.text = "Description: ${currentItem.description}"
        holder.textViewPrice.text = "Price: ${currentItem.price}"
        holder.imageViewGameImage.setImageBitmap(currentItem.gameImage?.let { BitmapFactory.decodeByteArray(currentItem.gameImage, 0, it.size) })

        // Show delete button only if the current user uploaded the post
        if (currentItem.creator == FirebaseAuth.getInstance().currentUser?.uid) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener {
                onDeleteClick(currentItem)
            }
        } else {
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount() = gamePosts.size
}
