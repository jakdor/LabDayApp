package com.jakdor.labday.view.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakdor.labday.R
import com.jakdor.labday.databinding.FragmentMediaBinding
import com.jakdor.labday.utils.GlideApp
import android.util.Log

/**
 * Displays LabDay connected media/social network channels
 * - Static content, no ViewModel required
 */
class MediaFragment : Fragment() {

    lateinit var binding: FragmentMediaBinding
    private val animationHandler = Handler()

    var testMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_media, container, false)

        GlideApp.with(this)
                .load(R.drawable.lab_day_logo_full)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.mediaSiteCard?.mediaItemImg)

        GlideApp.with(this)
                .load(R.drawable.yt_logo)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.mediaYtCard?.mediaItemImg)

        animateMenuItems()

        binding.mediaSiteCard?.mediaCard?.setOnClickListener {
            openWebsite(getString(R.string.link_lab_site))
        }
        binding.mediaFbCard?.mediaCard?.setOnClickListener {
            openFacebook(getString(R.string.link_fb_user))
        }
        binding.mediaInstagramCard?.mediaCard?.setOnClickListener {
            openInstagram(getString(R.string.link_intagram_user))
        }
        binding.mediaYtCard?.mediaCard?.setOnClickListener {
            openYoutube(getString(R.string.link_yt_id))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (testMode) return
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = getString(R.string.media_fragment_title)
        actionBar?.elevation = resources.getDimension(R.dimen.app_bar_elevation)
        actionBar?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        animationHandler.removeCallbacksAndMessages(null) //remove all callbacks
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

    /**
     * Try opening Facebook app, or if activity not found in web browser
     * @param user - facebook page
     */
    private fun openFacebook(user: String) {
        val appIntent = Intent(Intent.ACTION_VIEW)
        appIntent.data = Uri.parse("fb://facewebmodal/f?href=$user")

        try {
            appIntent.`package` = "com.facebook.katana"
            startActivity(appIntent)
        } catch (e: Exception) {
            appIntent.`package` = "com.facebook.lite"
        }

        try {
            startActivity(appIntent)
        } catch (e: Exception) {
            Log.i(CLASS_TAG, "Facebook app not installed")
            openWebsite("https://www.facebook.com/$user")
        }
    }

    /**
     * Try opening instagram profile in app, or if activity not found in web browser
     * @param user - instagram user profile
     */
    private fun openInstagram(user: String) {
        val uri = Uri.parse("http://instagram.com/_u/$user")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)
        likeIng.`package` = "com.instagram.android"

        try {
            startActivity(likeIng)
        } catch (e: Exception) {
            Log.i(CLASS_TAG, "Instagram app not found")
            openWebsite("http://www.instagram.com/$user")
        }
    }

    /**
     * Try opening youtube video in app, or if activity not found in web browser
     * @param videoId - yt file id
     */
    private fun openYoutube(videoId: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))

        try {
            startActivity(appIntent)
        } catch (e: Exception) {
            Log.i(CLASS_TAG, "Youtube app not found")
            openWebsite("http://www.youtube.com/watch?v=$videoId")
        }
    }

    /**
     * Animate items entry
     */
    private fun animateMenuItems() {
        val menuItems = arrayOf(binding.mediaSiteCard?.root!!,
                binding.mediaFbCard?.root!!,
                binding.mediaInstagramCard?.root!!,
                binding.mediaYtCard?.root!!)

        for (view in menuItems) {
            view.translationY = -50.0f
            view.translationX = -50.0f
            view.scaleX = 0.65f
            view.scaleY = 0.65f
            view.alpha = 0.0f
        }

        animationHandler.postDelayed(getNextAnimator(menuItems, 0), 5)
    }

    /**
     * Animate item
     * @param views card array
     * @param position current view to animate
     * @return Runnable
     */
    private fun getNextAnimator(views:Array<View>, position:Int): (() -> Unit)? {
        if (position >= views.size) return null
        var pos = position

        if(pos == 2){ //animate axis items simultaneously - material design guideline
            views[pos]
                    .animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .alpha(1.0f)
                    .translationX(0.0f)
                    .translationY(0.0f)
                    .setInterpolator(FastOutLinearInInterpolator())
                    .setDuration(200)
                    .start()
            ++pos
        }

        return { views[pos]
        .animate()
        .scaleX(1.0f)
        .scaleY(1.0f)
        .alpha(1.0f)
        .translationX(0.0f)
        .translationY(0.0f)
        .setInterpolator(FastOutLinearInInterpolator())
        .setDuration(200)
        .withEndAction(getNextAnimator(views, pos + 1))
        .start() }
    }

    companion object {
        const val CLASS_TAG = "MediaFragment"
    }
}