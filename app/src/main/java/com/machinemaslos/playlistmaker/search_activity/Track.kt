package com.machinemaslos.playlistmaker.search_activity

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackName") val name: String,
    @SerializedName("artistName") val artist: String,
    @SerializedName("trackTimeMillis") val time: String,
    @SerializedName("artworkUrl100") val artworkUrl: String,
    @SerializedName("trackId") val id: String
)