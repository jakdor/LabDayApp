package com.jakdor.labday.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import com.jakdor.labday.R
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
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
            Log.i(CLASS_TAG, "MapPath received from api!")
            drawPath(response.data)
            setCamPathBounds(response.data)
        } else {
            if (response.error != null) {
                Log.e(CLASS_TAG, response.error.toString())
            }
        }
    }

    /**
     * Draw received [MapPath] on map
     */
    private fun drawPath(mapPath: MapPath?){
        if(mapPath == null){
            Log.wtf(CLASS_TAG, "MapPath is null")
            return
        }

        val steps = mapPath.routes?.get(0)?.legs?.get(0)?.steps
        if(steps == null || !mapPath.status.equals("OK")){
            Log.e(CLASS_TAG, "Unable to plot path")
            return
        }

        val polylineOptions = PolylineOptions()
        val points: ArrayList<LatLng> = ArrayList()

        //add start point
        val startLat = steps[0].startLocation?.lat
        val startLong = steps[0].startLocation?.lng
        if(startLat == null || startLong == null){
            Log.e(CLASS_TAG, "Unable to plot path")
            return
        }
        points.add(LatLng(startLat, startLong))

        //add end points
        steps.forEach({ step ->
            val endLat = step.endLocation?.lat
            val endLong = step.endLocation?.lng
            if(endLat == null || endLong == null){
                Log.e(CLASS_TAG, "Unable to plot path")
                return
            }
            points.add(LatLng(endLat, endLong))
        })

        polylineOptions.addAll(points)
        polylineOptions.width(2.0f)
        polylineOptions.color(Color.BLUE)

        map?.addPolyline(polylineOptions)
    }

    /**
     * Sets optimal camera bounds for whole path
     */
    private fun setCamPathBounds(mapPath: MapPath?){
        if(mapPath == null){
            Log.wtf(CLASS_TAG, "MapPath is null")
            return
        }

        val neLat = mapPath.routes?.get(0)?.bounds?.northeast?.lat
        val neLong = mapPath.routes?.get(0)?.bounds?.northeast?.lng
        val swLat = mapPath.routes?.get(0)?.bounds?.southwest?.lat
        val swLong = mapPath.routes?.get(0)?.bounds?.southwest?.lng

        if(neLat == null || neLong == null || swLat == null || swLong == null){
            Log.e(CLASS_TAG, "Unable to set cam bounds")
            return
        }

        val latLngBounds = LatLngBounds(LatLng(swLat, swLong), LatLng(neLat, neLong))
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))
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
                .title(pointInfo)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker)))
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