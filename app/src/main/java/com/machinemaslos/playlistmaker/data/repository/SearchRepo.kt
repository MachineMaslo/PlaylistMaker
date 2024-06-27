package com.machinemaslos.playlistmaker.data.repository

import com.machinemaslos.playlistmaker.data.web.SearchApi
import com.machinemaslos.playlistmaker.data.web.TrackLookUpResponse
import com.machinemaslos.playlistmaker.data.web.TrackResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchRepo {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val searchApi = retrofit.create(SearchApi::class.java)

    fun search(trackName: String): Call<TrackResponse> {
        return searchApi.search(trackName)
    }

    fun lookUpSearch(trackId: String): Call<TrackLookUpResponse> {
        return searchApi.lookUpSearch(trackId)
    }

    companion object {
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}