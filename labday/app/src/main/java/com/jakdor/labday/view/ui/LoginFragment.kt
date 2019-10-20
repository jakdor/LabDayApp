package com.jakdor.labday.view.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

import com.jakdor.labday.R
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.di.InjectableFragment
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxStatus
import com.jakdor.labday.utils.GlideApp
import com.jakdor.labday.viewmodel.LoginViewModel

import javax.inject.Inject

import timber.log.Timber

import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.login_background.*
import kotlinx.android.synthetic.main.login_card.*

/**
 * First app-load login fragment
 */
class LoginFragment : Fragment(), InjectableFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var viewModel: LoginViewModel? = null

    private var loginLock = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.login_background, container, false)

        if (activity == null) {
            Timber.wtf("Unable to get Activity")
        } else {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                    or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimations()
        login_button.setOnClickListener { onLoginButtonClick() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel == null) {
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(LoginViewModel::class.java)

            observeLogin()
            observeLoadingStatus()
        }
    }

    fun startAnimations() {
        GlideApp.with(this)
                .load(getString(R.string.login_background_url))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .transition(withCrossFade())
                .into(login_background_image)

        val animation = AnimationUtils.loadAnimation(context, R.anim.login_anim)
        animation.interpolator = FastOutLinearInInterpolator()
        animation.start()

        val animation2 = AnimationUtils.loadAnimation(context, R.anim.login_card_anim)
        animation2.interpolator = FastOutLinearInInterpolator()
        animation2.start()

        val animation3 = AnimationUtils.loadAnimation(context, R.anim.login_text_anim)
        animation3.interpolator = FastOutLinearInInterpolator()
        animation3.start()

        login_logo.startAnimation(animation)
        login_card.startAnimation(animation2)
        logo_text.startAnimation(animation3)
    }

    fun loginInProgressAnimation(animStatus: Boolean) {
        if (animStatus) {
            if (login_loading_anim.visibility == View.GONE) {
                login_loading_anim.visibility = View.VISIBLE
            } else {
                Glide.with(this)
                        .asGif()
                        .load(R.drawable.load)
                        .into(login_loading_anim)
            }
        } else {
            login_loading_anim.visibility = View.GONE
        }
    }

    /**
     * Make login API call if inputs are valid
     */
    fun onLoginButtonClick() {
        if (!loginLock) {
            if (validateInputs()) {
                loginLock = true
                login_status_info.visibility = View.GONE

                hideKeyboard()

                viewModel!!.login(context, login_text_field.text.toString(),
                        password_text_field.text.toString())
            } else {
                login_status_info.setText(R.string.login_failed_empty_fields)
                login_status_info.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Basic login/password validation
     */
    fun validateInputs(): Boolean {
        return login_text_field.text.toString().isNotEmpty() &&
                password_text_field.text.toString().isNotEmpty()
    }

    /**
     * Hides softKeyboard
     */
    fun hideKeyboard() {
        try {
            if (activity == null)
                throw NullPointerException("getActivity() returned null")

            if (context == null)
                throw NullPointerException("getContext() returned null")

            val view = activity!!.currentFocus
                    ?: throw NullPointerException("Unable to get currentFocus view")

            val inputMethodManager =
                    context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        } catch (e: Exception) {
            Timber.wtf("Unable to hide softKeyboard, %s", e.toString())
        }

    }

    fun observeLogin() {
        viewModel!!.response.observe(this, Observer { this.switchToMainFragment(it) })
    }

    fun observeLoadingStatus() {
        viewModel!!.loadingStatus.observe(this, Observer<Boolean> { this.loginInProgressAnimation(it) })
    }

    fun switchToMainFragment(response: RxResponse<AppData>) {
        loginLock = false
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
        } else if (response.status == RxStatus.NO_INTERNET) {
            login_status_info.setText(R.string.login_no_internet)
            login_status_info.visibility = View.VISIBLE
            login_loading_anim.visibility = View.GONE
        } else {
            login_status_info.setText(R.string.unable_to_login)
            login_status_info.visibility = View.VISIBLE
            login_loading_anim.visibility = View.GONE
        }
    }

    fun setViewModel(viewModel: LoginViewModel) {
        this.viewModel = viewModel
    }
}