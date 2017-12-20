package com.jakdor.labday.view.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.jakdor.labday.viewmodel.SplashViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.splash, container, false);
        ButterKnife.bind(this, view);
        startAnimations();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SplashViewModel.class);

        observeLastUpdate();
        viewModel.loadAppData();
    }

    public void startAnimations(){
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.splash_anim);
        animation.start();

        splashLogo.clearAnimation();
        splashLogo.startAnimation(animation);
    }

    public void observeLastUpdate(){ //todo replace with last_update api call
        viewModel.getResponse().observe(this, this::switchToMainFragment);
    }

    public void switchToMainFragment(RxResponse<AppData> response){
        MainFragment mainFragment = new MainFragment();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                .addToBackStack(null)
                .replace(R.id.fragmentLayout, mainFragment)
                .commit();
    }
}
