package com.machinemaslos.playlistmaker.domain.useCases

import com.machinemaslos.playlistmaker.data.repository.SearchRepo
import com.machinemaslos.playlistmaker.data.web.TrackLookUpResponse
import com.machinemaslos.playlistmaker.data.web.TrackResponse
import retrofit2.Call

class SearchCase {
    private val searchRepo = SearchRepo()

    fun search(trackName: String): Call<TrackResponse> {
        return searchRepo.search(trackName)
    }

    fun lookUpSearch(trackId: String): Call<TrackLookUpResponse> {
        return searchRepo.lookUpSearch(trackId)
    }

}