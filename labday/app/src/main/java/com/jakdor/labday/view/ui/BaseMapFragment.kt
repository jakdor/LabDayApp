package com.jakdor.labday.view.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.jakdor.labday.R
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.Toast
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.location.LocationListener
import android.location.Criteria

/**
 * BaseMapFragment displays embedded google map with provided location marker
 * Extended by [MapFragment] and PlacesFragment
 */
abstract class BaseMapFragment : SupportMapFragment(), OnMapReadyCallback, LocationListener {

    protected var map: GoogleMap? = null

    //entry point to the Fused Location Provider
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    //location retrieved by the FusedLocationProvider
    private var lastKnownLocation: Location? = null

    //default location
    private val defaultLocation = LatLng(51.1085411, 17.0593825)
    private var locationPermissionGranted: Boolean = false

    //location updates
    private var locationUpdates: Boolean = false
    private var locationManager: LocationManager? = null
    private var provider: String? = null

    protected lateinit var oldBarTitle: String

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
        if(activity == null){
            Log.wtf(CLASS_TAG, "Unable to get Activity")
            return
        } else {
            this.getMapAsync(this) //triggers onMapReadyCallback
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        }

        checkGps()
    }

    override fun onResume() {
        super.onResume()
        val actionBar = (activity as AppCompatActivity).supportActionBar
        oldBarTitle = actionBar?.title as String
        actionBar.title = getString(R.string.map_fragment_bar)
        actionBar.show()
        if(!locationUpdates && locationPermissionGranted){

        }
    }

    override fun onPause() {
        super.onPause()
        locationManager?.removeUpdates(this)
        locationUpdates = false
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
     * Check GPS enabled, handle situation if gps offline
     */
    private fun checkGps(){
        val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //prompt user about gps turned off
            Toast.makeText(context, getString(R.string.gps_off_prompt), Toast.LENGTH_LONG).show()
        }
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
            locationManagerSetup()
            startLocationUpdates()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
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
                updateLocationUI()
                locationManagerSetup()
                startLocationUpdates()
            } else { //location not permitted by user, move to event marker
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM))
            }
        } else { //sth went wrong, or dialog dismissed
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM))
        }
    }

    /**
     * Config LocationManager
     */
    private fun locationManagerSetup() {
        //get location updates
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        provider = locationManager?.getBestProvider(criteria, false)
    }

    /**
     * Start receiving location updates
     */
    private fun startLocationUpdates() {
        try {
            //location update if min 100m distance
            locationManager?.requestLocationUpdates(provider, 2000, 100.0f, this)
            locationUpdates = true
        } catch (e: SecurityException){
            Log.e(CLASS_TAG, "Unauthorised call for location updates request")
        }
    }

    override fun onLocationChanged(p0: Location?) {
        map?.clear()
        useDeviceLocation()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    /**
     * Handle mapReady event
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //Other setup activities here

        updateLocationUI()
        useDeviceLocation() //getting gps lock may take some time, run any way using cached location
    }

    /**
     * Turn on the location layer and the related control on the map
     */
    private fun updateLocationUI() {
        if (map == null) {
            Log.wtf(CLASS_TAG, "GoogleMap is null")
            return
        }
        try {
            if (!locationPermissionGranted) {
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
    private fun useDeviceLocation() {
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
                                    .title("Ty"))!!
                                    .showInfoWindow()

                            //handle other functions requiring user location
                            onUserLocation(userLat, userLong)
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

    /**
     * Called if successfully determined user location
     */
    protected abstract fun onUserLocation(userLat: Double, userLong: Double)

    companion object {
        const val CLASS_TAG = "BaseMapFragment"
        const val DEFAULT_ZOOM = 17.0f
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}