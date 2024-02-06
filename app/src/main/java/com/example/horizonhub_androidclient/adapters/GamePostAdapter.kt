package com.example.horizonhub_androidclient.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.data.gamePost.GamePost
import com.squareup.picasso.Picasso

class GamePostAdapter(private var gamePosts: List<GamePost>) : RecyclerView.Adapter<GamePostAdapter.GamePostViewHolder>() {

    inner class GamePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCreator: TextView = itemView.findViewById(R.id.textViewCreator)
        val textViewGameName: TextView = itemView.findViewById(R.id.textViewGameName)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val imageViewGameImage: ImageView = itemView.findViewById(R.id.imageViewGameImage)
    }

    fun updateData(newGamePosts: List<GamePost>) {
        gamePosts = newGamePosts
        notifyDataSetChanged()
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
        Picasso.get().load(currentItem.gameImage).into(holder.imageViewGameImage)
    }

    override fun getItemCount() = gamePosts.size
}

