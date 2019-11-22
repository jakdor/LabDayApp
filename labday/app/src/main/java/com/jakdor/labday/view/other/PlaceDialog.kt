package com.jakdor.labday.view.other

import android.app.Dialog
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.jakdor.labday.R
import com.jakdor.labday.common.model.MapOther
import com.jakdor.labday.databinding.PlaceInfoDialogBinding
import android.view.Gravity
import com.jakdor.labday.view.ui.MapFragment
import com.jakdor.labday.utils.GlideApp

/**
 * Dialog displaying [MapOther] object
 */
class PlaceDialog(context: Context,
                  private val activity: FragmentActivity,
                  private val mapOther: MapOther) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding : PlaceInfoDialogBinding
                = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.place_info_dialog, null, false)

        binding.place = mapOther
        setContentView(binding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        GlideApp.with(binding.root)
                .load(mapOther.img)
                .placeholder(R.drawable.lab_day_logo_full)
                .centerCrop()
                .into(binding.placeImg)

        binding.placeMapFab.setOnClickListener {
            switchToMapFragment(mapOther.latitude, mapOther.longitude, mapOther.name)
        }
    }

    /**
     * Switch to [MapFragment] after dismissing dialog
     */
    private fun switchToMapFragment(lat: String, long: String, info: String){
        dismiss()
        activity.supportFragmentManager.beginTransaction()
                .addToBackStack(MapFragment.CLASS_TAG)
                .replace(R.id.fragmentLayout, MapFragment.newInstance(lat, long, info))
                .commit()

    }
}