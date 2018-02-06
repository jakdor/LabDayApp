package com.jakdor.labday.view.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.di.InjectableFragment;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;
import com.jakdor.labday.viewmodel.LoginViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * First app-load login fragment
 */
public class LoginFragment extends Fragment implements InjectableFragment {

    private final String CLASS_TAG = "LoginFragment";

    /**
     * Might as well be using DataBinding here, just for sake of showing that I know how to use ButterKnife
     */
    @BindView(R.id.login_logo)
    ImageView loginLogo;
    @BindView(R.id.login_card)
    CardView loginCard;
    @BindView(R.id.logo_text)
    TextView logoText;
    @BindView(R.id.login_background_image)
    ImageView background;
    @BindView(R.id.login_status_info)
    TextView loginStatusInfo;
    @BindView(R.id.login_text_field)
    TextView loginField;
    @BindView(R.id.password_text_field)
    TextView passwordField;
    @BindView(R.id.login_loading_anim)
    ImageView loadingAnim;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel viewModel;

    private boolean loginLock = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.login_background, container, false);
        ButterKnife.bind(this, view);

        if(getActivity() == null){
            Log.wtf(CLASS_TAG, "Unable to get Activity");
        }
        else{
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        startAnimations();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(viewModel == null) {
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(LoginViewModel.class);
        }

        observeLogin();
    }

    public void startAnimations(){
        Glide.with(this)
                .load(getString(R.string.login_background_url))
                .apply(new RequestOptions().centerCrop())
                .transition(withCrossFade())
                .into(background);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.login_anim);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        animation.start();

        Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.login_card_anim);
        animation2.setInterpolator(new FastOutLinearInInterpolator());
        animation2.start();

        Animation animation3 = AnimationUtils.loadAnimation(getContext(), R.anim.login_text_anim);
        animation3.setInterpolator(new FastOutLinearInInterpolator());
        animation3.start();

        loginLogo.startAnimation(animation);
        loginCard.startAnimation(animation2);
        logoText.startAnimation(animation3);
    }

    public void loginInProgressAnimation(){
        if(loadingAnim.getVisibility() == View.GONE){
            loadingAnim.setVisibility(View.VISIBLE);
        }
        else {
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.load)
                    .into(loadingAnim);
        }
    }

    /**
     * Make login API call if inputs are valid
     */
    @OnClick(R.id.login_button)
    public void onLoginButtonClick(){
        if(!loginLock) {
            if (validateInputs()) {
                loginLock = true;
                loginStatusInfo.setVisibility(View.GONE);

                hideKeyboard();
                loginInProgressAnimation();

                viewModel.login(getContext(), loginField.getText().toString(),
                        passwordField.getText().toString());
            }
            else {
                loginStatusInfo.setText(R.string.login_failed_empty_fields);
                loginStatusInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Basic login/password validation
     */
    public boolean validateInputs(){
        return !loginField.getText().toString().isEmpty()
                && !passwordField.getText().toString().isEmpty();
    }

    /**
     * Hides softKeyboard
     */
    public void hideKeyboard(){
        try {
            if(getActivity() == null)
                throw new NullPointerException("getActivity() returned null");

            if(getContext() == null)
                throw new NullPointerException("getContext() returned null");

            View view = getActivity().getCurrentFocus();

            if(view == null)
                throw new NullPointerException("Unable to get currentFocus view");

            InputMethodManager inputMethodManager = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(inputMethodManager == null)
                throw new NullPointerException("Unable to get InputMethodManager service");

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
        catch (Exception e){
            Log.wtf(CLASS_TAG, "Unable to hide softKeyboard, " + e.toString());
        }
    }

    public void observeLogin(){
        viewModel.getResponse().observe(this, this::switchToMainFragment);
    }

    public void switchToMainFragment(RxResponse<AppData> response){
        loginLock = false;
        if(response.status == RxStatus.SUCCESS || response.status == RxStatus.SUCCESS_DB) {
            MainFragment mainFragment = new MainFragment();

            if(getActivity() == null){
                Log.wtf(CLASS_TAG, "Unable to get Activity");
                return;
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                    .addToBackStack(null)
                    .replace(R.id.fragmentLayout, mainFragment)
                    .commit();
        }
        else if(response.status == RxStatus.NO_INTERNET){
            loginStatusInfo.setText(R.string.login_no_internet);
            loginStatusInfo.setVisibility(View.VISIBLE);
            loadingAnim.setVisibility(View.GONE);
        }
        else {
            loginStatusInfo.setText(R.string.unable_to_login);
            loginStatusInfo.setVisibility(View.VISIBLE);
            loadingAnim.setVisibility(View.GONE);
        }
    }

    public void setViewModel(LoginViewModel viewModel) {
        this.viewModel = viewModel;
    }
}