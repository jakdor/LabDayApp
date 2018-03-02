package com.jakdor.labday.view.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.jakdor.labday.R

/**
 * MapFragment displays embedded google map with provided location marker
 * - no ViewModel required - lat/long provided in newInstance()
 */
class MapFragment : SupportMapFragment() {

    private lateinit var oldBarTitle: String

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        var rootView = super.onCreateView(inflater, container, bundle)
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_map, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }

    companion object {
        const val CLASS_TAG = "MapFragment"
    }
}