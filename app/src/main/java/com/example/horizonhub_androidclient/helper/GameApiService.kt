package com.example.horizonhub_androidclient.helper

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GameApiService {
    @GET("games")
    fun getGamesByTitle(@Query("title") title: String): Call<List<GamePrices>>
}
