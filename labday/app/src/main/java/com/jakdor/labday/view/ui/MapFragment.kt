package com.jakdor.labday.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.jakdor.labday.R
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jakdor.labday.common.model.maps.MapPath
import com.jakdor.labday.di.InjectableFragment
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxStatus
import com.jakdor.labday.viewmodel.MapViewModel

import javax.inject.Inject

/**
 * MapFragment extends [BaseMapFragment] - shows user location and path to position provided in
 * newInstance() arguments
 */
class MapFragment : BaseMapFragment(), InjectableFragment {

    private var viewModel: MapViewModel? = null

    private lateinit var pointLatitude: String
    private lateinit var pointLongitude: String
    private lateinit var pointInfo: String

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    /**
     * get arguments
     */
    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)

        //get arguments
        val argTriple = arguments?.get("mapData") as Triple<*,*,*>
        pointLatitude = argTriple.first as String
        pointLongitude = argTriple.second as String
        pointInfo = argTriple.third as String
    }

    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)

        if(viewModel == null){
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(MapViewModel::class.java)
        }

        observeData()
    }

    /**
     * Observer viewModel for [MapPath]
     */
    private fun observeData(){
        viewModel?.mapPath?.observe(this, Observer<RxResponse<MapPath>> { t ->
            if(t != null){
                processResponse(t)
            } else {
                Log.e(CLASS_TAG, "RxResponse returned null")
            }
        })
    }

    private fun processResponse(response: RxResponse<MapPath>){
        if (response.status == RxStatus.SUCCESS) {
            Log.i(CLASS_TAG, "got MapPath from api!")
            //todo draw path on map :)
        } else {
            if (response.error != null) {
                Log.e(CLASS_TAG, response.error.toString())
            }
        }
    }

    /**
     * Handle mapReady event
     */
    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        setEventMarker()
    }

    /**
     * Sets event marker by provided location in fragment arguments
     */
    private fun setEventMarker() {
        map?.addMarker(MarkerOptions()
                .position(LatLng(pointLatitude.toDouble(), pointLongitude.toDouble()))
                .title(pointInfo))
    }


    override fun onUserLocation(userLat: Double, userLong: Double) {
        makePathApiCall(userLat.toString(), userLong.toString())
    }

    private fun makePathApiCall(originLat: String, originLong: String){
        val apiKey = getString(R.string.gsm_maps_key) //todo api key safe storage on device
        viewModel?.makePathRequest(originLat, originLong, pointLatitude, pointLongitude, apiKey)
    }

    companion object {
        const val CLASS_TAG = "MapFragment"

        fun newInstance(lat: String, long: String, info: String): MapFragment{
            val mapFragment = MapFragment()

            val bundle = Bundle()
            bundle.putSerializable("mapData", Triple(lat, long, info))
            mapFragment.arguments = bundle

            return mapFragment
        }
    }
}