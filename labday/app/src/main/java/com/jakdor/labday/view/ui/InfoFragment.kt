package com.jakdor.labday.view.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakdor.labday.R
import com.jakdor.labday.databinding.FragmentInfoBinding

/**
 * Fragment displaying authors info
 * - static content no ViewModel required
 */
class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    var testMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (testMode) return
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = getString(R.string.info_fragment_title)
        actionBar?.elevation = resources.getDimension(R.dimen.app_bar_elevation)
        actionBar?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (testMode) return
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.elevation = 0.0f
        actionBar?.hide()
    }

    companion object {
        const val CLASS_TAG = "InfoFragment"
    }
}