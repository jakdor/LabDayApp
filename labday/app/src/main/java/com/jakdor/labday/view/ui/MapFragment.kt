package com.jakdor.labday.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.jakdor.labday.R
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.jakdor.labday.common.model.maps.MapPath
import com.jakdor.labday.di.InjectableFragment
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxStatus
import com.jakdor.labday.viewmodel.MapViewModel

import javax.inject.Inject

/**
 * MapFragment displays embedded google map with provided location marker
 * - no ViewModel required - lat/long provided in newInstance()
 */
class MapFragment : SupportMapFragment(), OnMapReadyCallback, InjectableFragment {

    private var map: GoogleMap? = null

    //entry point to the Fused Location Provider
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    //location retrieved by the FusedLocationProvider
    private var lastKnownLocation: Location? = null

    //default location
    private val defaultLocation = LatLng(51.1085411, 17.0593825)
    private var locationPermissionGranted: Boolean = false

    private lateinit var oldBarTitle: String
    private var viewModel: MapViewModel? = null

    private lateinit var pointLatitude: String
    private lateinit var pointLongitude: String
    private lateinit var pointInfo: String

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        var rootView = super.onCreateView(inflater, container, bundle)
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_map, container, false)
        return rootView
    }

    /**
     * Begin setup, get fusedLocationProviderClient
     */
    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)

        //get arguments
        val argTriple = arguments?.get("mapData") as Triple<*,*,*>
        pointLatitude = argTriple.first as String
        pointLongitude = argTriple.second as String
        pointInfo = argTriple.third as String

        if(activity == null){
            Log.wtf(CLASS_TAG, "Unable to get Activity")
            return
        }

        this.getMapAsync(this) //triggers onMapReadyCallback
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)

        if(viewModel == null){
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(MapViewModel::class.java)
        }

        observeData()
    }

    override fun onResume() {
        super.onResume()
        val actionBar = (activity as AppCompatActivity).supportActionBar
        oldBarTitle = actionBar?.title as String
        actionBar.title = getString(R.string.map_fragment_bar)
        actionBar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = oldBarTitle
        actionBar?.hide()

        //cleanup
        try{
            map?.isMyLocationEnabled = false
        } catch (e: SecurityException){
            Log.wtf(CLASS_TAG, "SecurityException thrown during onDestroy, " + e.toString())
        }
    }

    override fun onDestroyView() {
        map?.clear()
        super.onDestroyView()
    }

    /**
     * Observer viewModel for [MapPath]
     */
    fun observeData(){
        viewModel?.mapPath?.observe(this, Observer<RxResponse<MapPath>> { t ->
            if(t != null){
                processResponse(t)
            } else {
                Log.e(CLASS_TAG, "RxResponse returned null")
            }
        })
    }

    fun processResponse(response: RxResponse<MapPath>){
        if (response.status == RxStatus.SUCCESS) {
            Log.i(CLASS_TAG, "got MapPath from api!")
            //todo draw path on map :)
        } else {
            if (response.error != null) {
                Log.e(CLASS_TAG, response.error.toString())
            }
        }
    }

    fun makePathApiCall(originLat: String, originLong: String){
        val apiKey = getString(R.string.gsm_maps_key) //todo api key safe storage on device
        viewModel?.makePathRequest(originLat, originLong, pointLatitude, pointLongitude, apiKey)
    }

    /**
     * Request location permission
     * The result of the permission request is handled by a callback, onRequestPermissionsResult()
     */
    private fun getLocationPermission() {
        if(activity == null){
            Log.wtf(CLASS_TAG, "Unable to get Activity")
            return
        }

        if (ContextCompat.checkSelfPermission(activity!!.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Handle received permission
     */
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        locationPermissionGranted = false
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION){
            //If request is cancelled, the result arrays are empty
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true
            }
        }
        updateLocationUI()
    }

    /**
     * Handle mapReady event
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //Other setup activities here

        setEventMarker()
        updateLocationUI()
        getDeviceLocation()
    }

    /**
     * Sets event marker by provided location in fragment arguments
     */
    private fun setEventMarker() {
        map?.addMarker(MarkerOptions()
                .position(LatLng(pointLatitude.toDouble(), pointLongitude.toDouble()))
                .title(pointInfo))
    }

    /**
     * Turn on the My Location layer and the related control on the map
     */
    private fun updateLocationUI() {
        if (map == null) {
            Log.wtf(CLASS_TAG, "GoogleMap is null")
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e(CLASS_TAG, e.toString())
        }
    }

    /**
     * Get bast available device location, set position on the map
     * - might be null if location is not available
     */
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient?.lastLocation
                locationResult?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result

                        if(lastKnownLocation == null) {
                            Log.wtf(CLASS_TAG, "LastKnownLocation was null")
                        } else {
                            val userLat = lastKnownLocation!!.latitude
                            val userLong = lastKnownLocation!!.longitude

                            //set the map's camera position to the current location of the device
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    LatLng(userLat, userLong), DEFAULT_ZOOM))
                            //mark device location on the map
                            map?.addMarker(MarkerOptions()
                                    .position(LatLng(userLat, userLong))
                                    .title("marker title"))
                                    //.icon(BitmapDescriptorFactory.fromResource())

                            //make path api call with user location
                            makePathApiCall(userLat.toString(), userLong.toString())
                        }
                    } else {
                        Log.i(CLASS_TAG, "Current location returned null - using default")
                        Log.e(CLASS_TAG, "Exception: %s", task.exception)
                        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }

            }
        } catch (e: SecurityException) {
            Log.e(CLASS_TAG, e.toString())
        }
    }

    companion object {
        const val CLASS_TAG = "MapFragment"
        private const val DEFAULT_ZOOM = 17.0f
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        fun newInstance(lat: String, long: String, info: String): MapFragment{
            val mapFragment = MapFragment()

            val bundle = Bundle()
            bundle.putSerializable("mapData", Triple(lat, long, info))
            mapFragment.arguments = bundle

            return mapFragment
        }
    }
}