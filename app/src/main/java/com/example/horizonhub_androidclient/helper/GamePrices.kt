package com.example.horizonhub_androidclient.helper

import com.google.gson.annotations.SerializedName

data class GamePrices(
    @SerializedName("gameID") val id: String,
    @SerializedName("steamAppID") val steamAppID: String?,
    @SerializedName("cheapest") val cheapest: String,
    @SerializedName("cheapestDealID") val cheapestDealID: String,
    @SerializedName("external") val external: String,
    @SerializedName("internalName") val internalName: String,
    @SerializedName("thumb") val thumb: String
)
