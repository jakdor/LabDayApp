package com.jakdor.labday.view.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.di.InjectableFragment;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;
import com.jakdor.labday.viewmodel.LoginViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;

/**
 * First use login fragment
 */
public class LoginFragment extends Fragment implements InjectableFragment {

    @BindView(R.id.login_logo)
    ImageView loginLogo;
    @BindView(R.id.loginCard)
    CardView loginCard;
    @BindView(R.id.logo_text)
    TextView logoText;
    @BindView(R.id.login_background_image)
    ImageView background;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.login_background, container, false);
        ButterKnife.bind(this, view);
        startAnimations();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(LoginViewModel.class);
    }

    public void startAnimations(){
        Glide.with(this)
                .load(getString(R.string.login_background_url))
                .centerCrop()
                .crossFade()
                .into(background);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.login_anim);
        animation.start();

        Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.login_card_anim);
        animation2.start();

        Animation animation3 = AnimationUtils.loadAnimation(getContext(), R.anim.login_text_anim);
        animation3.start();

        loginLogo.startAnimation(animation);
        loginCard.startAnimation(animation2);
        logoText.startAnimation(animation3);
    }

    public void observeLogin(){
        viewModel.getResponse().observe(this, this::switchToMainFragment);
    }

    public void switchToMainFragment(RxResponse<AppData> response){
        if(response.status == RxStatus.SUCCESS) {
            MainFragment mainFragment = new MainFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                    .addToBackStack(null)
                    .replace(R.id.fragmentLayout, mainFragment)
                    .commit();
        }
        else {
            Toast.makeText(getContext(), R.string.unable_to_login, Toast.LENGTH_SHORT).show();
        }
    }
}