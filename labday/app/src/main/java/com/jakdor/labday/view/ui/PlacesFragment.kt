package com.jakdor.labday.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jakdor.labday.R
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.common.model.MapOther
import com.jakdor.labday.di.InjectableFragment
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxStatus
import com.jakdor.labday.viewmodel.PlacesViewModel
import com.jakdor.labday.view.utils.PlaceDialog
import javax.inject.Inject

/**
 * PlacesFragment - displays other POI on PWR campus
 */
class PlacesFragment : BaseMapFragment(), InjectableFragment {

    private var viewModel: PlacesViewModel? = null
    private var markers: List<MapOther>? = null
    private var mapReady: Boolean = false

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
        if (response.status == RxStatus.SUCCESS || response.status == RxStatus.SUCCESS_DB) {
            if(response.data?.mapOthers != null) {
                markers = response.data.mapOthers
                if(mapReady)
                    loadMarkers(markers!!)
            } else {
                Log.e(CLASS_TAG, "Empty MapOthers list")
            }
        } else {
            if (response.error != null) {
                Log.e(CLASS_TAG, response.error.toString())
            }
        }
    }

    /**
     * Place markers on the map
     */
    fun loadMarkers(places: List<MapOther>){
        places.forEach { mapOther: MapOther ->
            var drawableId : Int = R.mipmap.map_marker
            when(mapOther.type){
                1 -> drawableId = R.mipmap.info_marker
                2 -> drawableId = R.mipmap.food_marker
                3 -> drawableId = R.mipmap.rest_marker
            }

            map?.addMarker(MarkerOptions()
                    .position(LatLng(mapOther.latitude.toDouble(), mapOther.longitude.toDouble()))
                    .title(mapOther.name)
                    .icon(BitmapDescriptorFactory.fromResource(drawableId)))
        }

        map?.setOnMarkerClickListener { marker ->
            places.forEach { mapOther: MapOther ->
                if(mapOther.name == marker.title)
                    showMarkerInfo(mapOther)
            }
            true
        }
    }

    /**
     * Display place info dialog
     */
    fun showMarkerInfo(place: MapOther){
        if(context != null && activity != null)
            PlaceDialog(context!!, activity as FragmentActivity, place).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        mapReady = true
        if(markers != null){
            loadMarkers(markers!!)
        }
    }

    override fun onUserLocation(userLat: Double, userLong: Double) {
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM))
        if(markers != null && mapReady){
            loadMarkers(markers!!)
        }
    }

    companion object {
        const val CLASS_TAG = "PlacesFragment"
        const val DEFAULT_ZOOM = 16.0f
    }
}