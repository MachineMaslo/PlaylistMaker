package com.machinemaslos.playlistmaker.search_activity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    @SerializedName("trackName") val name: String,
    @SerializedName("artistName") val artist: String,
    @SerializedName("trackTimeMillis") val time: String,
    @SerializedName("artworkUrl100") val artworkUrl: String,
    @SerializedName("trackId") val id: String,
)

data class ExtendedTrack(
    @SerializedName("trackName") val name: String,
    @SerializedName("artistName") val artist: String,
    @SerializedName("trackTimeMillis") val time: String,
    @SerializedName("artworkUrl100") val artworkUrl: String,
    @SerializedName("trackId") val id: String,
    @SerializedName("collectionName") val album: String,
    @SerializedName("releaseDate") val year: String,
    @SerializedName("primaryGenreName") val genre: String,
    @SerializedName("country") val country: String,
    @SerializedName("previewUrl") val songUrl: String
): Serializable