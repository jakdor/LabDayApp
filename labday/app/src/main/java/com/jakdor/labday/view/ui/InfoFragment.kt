package com.jakdor.labday.view.ui

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.jakdor.labday.R
import com.jakdor.labday.databinding.FragmentInfoBinding
import com.jakdor.labday.utils.GlideApp

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

        GlideApp.with(this)
                .load(getString(R.string.link_asi_logo))
                .placeholder(R.drawable.lab_day_logo_full)
                .fitCenter()
                .into(binding.infoLogo)

        GlideApp.with(this)
                .load(getString(R.string.link_img_kuba))
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.authorKuba.infoItemImg)

        GlideApp.with(this)
                .load(getString(R.string.link_img_jan))
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.authorJan.infoItemImg)

        GlideApp.with(this)
                .load(getString(R.string.link_img_org1))
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.org1.infoItemImg)

        GlideApp.with(this)
                .load(getString(R.string.link_img_org2))
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.org2.infoItemImg)

        GlideApp.with(this)
                .load(getString(R.string.link_img_org3))
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.org3.infoItemImg)

        binding.infoLogo.setOnClickListener { openWebsite(getString(R.string.link_asi)) }
        binding.authorKuba.infoItemGh.setOnClickListener {
            openWebsite(getString(R.string.link_gh_kuba))
        }
        binding.authorJan.infoItemGh.setOnClickListener {
            openWebsite(getString(R.string.link_gh_jan))
        }

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

    /**
     * Lunch open website intent
     * @param url - website url
     */
    private fun openWebsite(url: String) {
        context?.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    }

    companion object {
        const val CLASS_TAG = "InfoFragment"
    }
}