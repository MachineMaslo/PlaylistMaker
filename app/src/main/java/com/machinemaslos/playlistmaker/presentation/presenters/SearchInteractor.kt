package com.machinemaslos.playlistmaker.presentation.presenters

import com.machinemaslos.playlistmaker.data.web.TrackLookUpResponse
import com.machinemaslos.playlistmaker.data.web.TrackResponse
import com.machinemaslos.playlistmaker.domain.useCases.SearchCase
import com.machinemaslos.playlistmaker.presentation.ui.SearchActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchInteractor(private val activity: SearchActivity) {
    private val searchCase = SearchCase()

    fun search() {
        searchCase.search(activity.searchText).enqueue(object : Callback<TrackResponse> {

                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    activity.setTracksLoadingState(false)

                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            activity.setTracks(response.body()?.results!!)
                        }
                        else { activity.showNothingFoundError() }
                    }
                    else { activity.showInternetConnectionError() }

                    activity.notifyTrackListChanged()
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    activity.setTracksLoadingState(false)
                    activity.showInternetConnectionError()
                }

            })

    }

    fun lookUpSearch(trackId: String) {

        searchCase.lookUpSearch(trackId).enqueue(object: Callback<TrackLookUpResponse> {

                override fun onResponse(call: Call<TrackLookUpResponse>, response: Response<TrackLookUpResponse>) {

                    if (response.code() == 200)
                        activity.goToPlayer(response.body()?.results?.firstOrNull()!!)

                }

                override fun onFailure(call: Call<TrackLookUpResponse>, t: Throwable) {}
            })

    }

}