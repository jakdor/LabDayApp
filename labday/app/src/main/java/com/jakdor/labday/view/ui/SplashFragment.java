package com.jakdor.labday.view.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.di.InjectableFragment;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;
import com.jakdor.labday.viewmodel.SplashViewModel;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Loaded in MainActivity prepares {@link com.jakdor.labday.common.repository.ProjectRepository}
 * while displaying welcome logo/animations, provides seamless transition between
 * {@link SplashActivity} and {@link MainActivity}
 * */
public class SplashFragment extends Fragment implements InjectableFragment {

    @BindView(R.id.splash_logo)
    ImageView splashLogo;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SplashViewModel viewModel;

    private Handler delayedTransactionHandler = new Handler();
    private Runnable runnable = this::switchToLoginFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.splash, container, false);
        ButterKnife.bind(this, view);
        startAnimations();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(viewModel == null) {
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(SplashViewModel.class);
        }

        if(viewModel.isLoggedIn(getContext())) {
            observeLastUpdate();
            viewModel.updateAppData(getContext());
        }
        else {
            Timber.i("No access token, switching to loginFragment");
            delayedTransactionHandler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        delayedTransactionHandler.removeCallbacks(runnable);
    }

    public void startAnimations(){
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.splash_anim);
        animation.setInterpolator(new LinearOutSlowInInterpolator());
        animation.start();

        splashLogo.clearAnimation();
        splashLogo.startAnimation(animation);
    }

    public void observeLastUpdate(){
        viewModel.getResponse().observe(this, this::switchToMainFragment);
    }

    public void switchToMainFragment(RxResponse<AppData> response){
        if(response.status == RxStatus.SUCCESS || response.status == RxStatus.SUCCESS_DB) {
            MainFragment mainFragment = new MainFragment();

            if(getActivity() == null){
                Timber.wtf("Unable to get Activity");
                return;
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_in_slow, R.anim.fragment_fade_out)
                    .addToBackStack(null)
                    .replace(R.id.fragmentLayout, mainFragment)
                    .commit();
        }
        else {
            Timber.wtf("Unable to get repo data, fallback to login");
            switchToLoginFragment();
        }
    }

    public void switchToLoginFragment(){
        LoginFragment loginFragment = new LoginFragment();

        if(getActivity() == null){
            Timber.wtf("Unable to get Activity");
            return;
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                .addToBackStack(null)
                .replace(R.id.fragmentLayout, loginFragment)
                .commit();
    }

    public void setViewModel(SplashViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public Handler getDelayedTransactionHandler() {
        return delayedTransactionHandler;
    }
}
