package com.machinemaslos.playlistmaker.search_activity

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>

}