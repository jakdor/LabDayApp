package com.jakdor.labday.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.jakdor.labday.R
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.common.model.MapOther
import com.jakdor.labday.di.InjectableFragment
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxStatus
import com.jakdor.labday.viewmodel.PlacesViewModel
import javax.inject.Inject

/**
 * PlacesFragment - displays other POI on PWR campus
 */
class PlacesFragment : BaseMapFragment(), InjectableFragment {

    var viewModel: PlacesViewModel? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        barTitle = getString(R.string.places_fragment_bar)
    }

    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)

        if(viewModel == null){
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(PlacesViewModel::class.java)
        }

        observeData()
        viewModel?.loadAppData(context)
    }

    /**
     * Observe Event/Speaker pair
     */
    fun observeData() {
        viewModel?.response?.observe(this, Observer {
            t -> if(t != null) processResponse(t)
            else Log.e(CLASS_TAG, "RxResponse returned null")
        })
    }

    /**
     * Checks received data
     */
    fun processResponse(response: RxResponse<AppData>) {
        if (response.status == RxStatus.SUCCESS) {
            if(response.data?.mapOthers != null) {
                loadMarkers(response.data.mapOthers)
            } else {
                Log.e(CLASS_TAG, "Empty MapOthers list")
            }
        } else {
            if (response.error != null) {
                Log.e(CLASS_TAG, response.error.toString())
            }
        }
    }

    fun loadMarkers(places: List<MapOther>){

    }

    override fun onUserLocation(userLat: Double, userLong: Double) {}

    companion object {
        const val CLASS_TAG = "PlacesFragment"
    }
}