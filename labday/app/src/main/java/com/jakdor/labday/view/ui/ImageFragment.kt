package com.jakdor.labday.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

import com.jakdor.labday.R
import com.jakdor.labday.utils.GlideApp

/**
 * Fragment displaying image from url provided in newInstance()
 * - No ViewModel required
 */
class ImageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = layoutInflater.inflate(R.layout.fragment_img, container, false)

        val animView = view.findViewById<ImageView>(R.id.image_loading_anim)
        val imgView = view.findViewById<ImageView>(R.id.image)
        val imgPair = arguments?.getSerializable("imgPair") as Pair<*, *>

        GlideApp.with(this)
                .asGif()
                .load(R.drawable.load)
                .into(animView)

        GlideApp.with(this)
                .load(imgPair.first)
                .fitCenter()
                .transition(withCrossFade())
                .into(imgView)

        setActionBar(imgPair.second as String)
        return view
    }

    private fun setActionBar(title: String){
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.elevation = resources.getDimension(R.dimen.app_bar_elevation)
        actionBar?.show()
        actionBar?.title = title
    }

    override fun onDestroy() {
        super.onDestroy()
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.hide()
        actionBar?.elevation = 0.0f
    }

    companion object {
        const val CLASS_TAG = "ImageFragment"

        fun newInstance(imgUrl: String, title: String) : ImageFragment {
            val imageFragment = ImageFragment()

            val bundle = Bundle()
            bundle.putSerializable("imgPair", Pair(imgUrl, title))
            imageFragment.arguments = bundle

            return imageFragment
        }
    }
}