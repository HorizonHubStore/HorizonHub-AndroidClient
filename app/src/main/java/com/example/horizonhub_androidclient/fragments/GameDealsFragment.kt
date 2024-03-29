package com.example.horizonhub_androidclient.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.helper.GameDealsApiService
import com.example.horizonhub_androidclient.helper.GameDeal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameDealsFragment : Fragment(R.layout.fragment_game_deals) {
    private lateinit var gameApiService: GameDealsApiService
    private lateinit var editTextGameTitle: EditText
    private lateinit var recyclerViewGames: RecyclerView
    private lateinit var gameListAdapter: GameListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_game_deals, container, false)

        editTextGameTitle = rootView.findViewById(R.id.editTextGameTitle)
        recyclerViewGames = rootView.findViewById(R.id.recyclerViewGames)
        recyclerViewGames.layoutManager = LinearLayoutManager(requireContext())
        gameListAdapter = GameListAdapter()
        recyclerViewGames.adapter = gameListAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cheapshark.com/api/1.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        gameApiService = retrofit.create(GameDealsApiService::class.java)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.buttonSearch).setOnClickListener {
            val title = editTextGameTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                getGames(title)
            }
        }
    }

    private fun getGames(title: String) {
        val call = gameApiService.getGamesByTitle(title)
        call.enqueue(object : Callback<List<GameDeal>> {
            override fun onResponse(
                call: Call<List<GameDeal>>,
                response: Response<List<GameDeal>>
            ) {
                if (response.isSuccessful) {
                    val games = response.body()
                    games?.let {
                        gameListAdapter.submitList(it)
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<List<GameDeal>>, t: Throwable) {
            }
        })
    }

    inner class GameListAdapter : RecyclerView.Adapter<GameListAdapter.GameViewHolder>() {

        private var games: List<GameDeal> = listOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.game_deals_item, parent, false)
            return GameViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
            val game = games[position]

            holder.textViewGameTitle.text = game.external
            holder.textViewGameID.text = resources.getString(R.string.game_id_format, game.id)
            holder.textViewCheapestPrice.text =
                resources.getString(R.string.cheapest_price_format, game.cheapest)

            Glide.with(requireContext())
                .load(game.thumb)
                .placeholder(R.drawable.game_no_image)
                .error(R.drawable.error_image)
                .into(holder.imageViewGameThumbnail)
        }

        override fun getItemCount(): Int = games.size

        fun submitList(newList: List<GameDeal>) {
            games = newList
            notifyDataSetChanged()
        }

        inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewGameTitle: TextView = itemView.findViewById(R.id.textViewGameTitle)
            val textViewGameID: TextView = itemView.findViewById(R.id.textViewGameID)
            val textViewCheapestPrice: TextView = itemView.findViewById(R.id.textViewCheapestPrice)
            val imageViewGameThumbnail: ImageView =
                itemView.findViewById(R.id.imageViewGameThumbnail)

            init {
                itemView.setOnClickListener {
                    val game = games[adapterPosition]
                    val dealID = game.cheapestDealID
                    val bestPricePageUrl = "https://www.cheapshark.com/redirect?dealID=$dealID"
                    openBestPricePage(bestPricePageUrl)
                }
            }

            private fun openBestPricePage(url: String) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }
}