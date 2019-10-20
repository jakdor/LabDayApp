package com.jakdor.labday.view.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils

import com.jakdor.labday.R
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.di.InjectableFragment
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxStatus
import com.jakdor.labday.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.splash.*

import javax.inject.Inject
import timber.log.Timber

/**
 * Loaded in MainActivity prepares [com.jakdor.labday.common.repository.ProjectRepository]
 * while displaying welcome logo/animations, provides seamless transition between
 * [SplashActivity] and [MainActivity]
 */
class SplashFragment : Fragment(), InjectableFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var viewModel: SplashViewModel? = null

    val delayedTransactionHandler = Handler()
    private val runnable = Runnable { this.switchToLoginFragment() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimations()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel == null) {
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(SplashViewModel::class.java)
        }

        if (viewModel!!.isLoggedIn(context)) {
            observeLastUpdate()
            viewModel!!.updateAppData(context)
        } else {
            Timber.i("No access token, switching to loginFragment")
            delayedTransactionHandler.postDelayed(runnable, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        delayedTransactionHandler.removeCallbacks(runnable)
    }

    fun startAnimations() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.splash_anim)
        animation.interpolator = LinearOutSlowInInterpolator()
        animation.start()

        splash_logo.clearAnimation()
        splash_logo.startAnimation(animation)
    }

    fun observeLastUpdate() {
        viewModel!!.response.observe(this, androidx.lifecycle.Observer { this.switchToMainFragment(it) })
    }

    fun switchToMainFragment(response: RxResponse<AppData>) {
        if (response.status == RxStatus.SUCCESS || response.status == RxStatus.SUCCESS_DB) {
            val mainFragment = MainFragment()

            if (activity == null) {
                Timber.wtf("Unable to get Activity")
                return
            }

            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.popBackStack()
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_in_slow, R.anim.fragment_fade_out)
                    .addToBackStack(null)
                    .replace(R.id.fragmentLayout, mainFragment)
                    .commit()
        } else {
            Timber.wtf("Unable to get repo data, fallback to login")
            switchToLoginFragment()
        }
    }

    fun switchToLoginFragment() {
        val loginFragment = LoginFragment()

        if (activity == null) {
            Timber.wtf("Unable to get Activity")
            return
        }

        val fragmentManager = activity!!.supportFragmentManager
        fragmentManager.popBackStack()
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                .addToBackStack(null)
                .replace(R.id.fragmentLayout, loginFragment)
                .commit()
    }

    fun setViewModel(viewModel: SplashViewModel) {
        this.viewModel = viewModel
    }
}
