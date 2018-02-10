package com.jakdor.labday.view.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.Path;
import com.jakdor.labday.databinding.FragmentMainBinding;
import com.jakdor.labday.di.InjectableFragment;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;
import com.jakdor.labday.viewmodel.MainViewModel;

import javax.inject.Inject;

/**
 * MainActivity landing fragment
 */
public class MainFragment extends Fragment implements InjectableFragment {

    private final String CLASS_TAG = "MainFragment";

    private FragmentMainBinding binding;
    private Handler animationHandler = new Handler();

    private MainViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        Glide.with(this)
                .load(R.drawable.lab_day_logo_full)
                .into(binding.menuLogo);

        animateMenuItems();

        binding.menuTimetable.menuCard.setOnClickListener(view -> onTimetableCardClick());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(viewModel == null) {
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(MainViewModel.class);
        }

        observeAppData();
        viewModel.loadAppData(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        animationHandler.removeCallbacksAndMessages(null); //remove all callbacks
    }

    public void observeAppData(){
        viewModel.getResponse().observe(this, this::processResponse);
    }

    /**
     * Set path name in timetable card
     */
    private void processResponse(RxResponse<AppData> response) {
        if(response.status == RxStatus.SUCCESS || response.status == RxStatus.SUCCESS_DB){
            if(response.data != null) {
                for(Path path : response.data.getPaths()){
                    if(path.getActive() == 1){
                        binding.setPath(path.getName());
                        break;
                    }
                }
            }
        }
        else {
            if(response.error != null) {
                Log.e(CLASS_TAG, response.error.toString());
            }
            binding.setPath("");
        }
    }

    /**
     * Animate logo and menu cards
     */
    private void animateMenuItems(){
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.main_logo_anim);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        animation.start();

        binding.menuLogo.startAnimation(animation);

        View[] menuItems = {
                binding.menuTimetable.getRoot(),
                binding.menuMap.getRoot(),
                binding.menuMedia.getRoot(),
                binding.menuInfo.getRoot()
        };

        for (View view : menuItems){
            view.setTranslationY(50.0f);
            view.setScaleX(0.75f);
            view.setScaleY(0.75f);
            view.setAlpha(0.0f);
        }

        animationHandler.postDelayed(getNextAnimator(menuItems, 0), 5);
    }

    /**
     * Animate menu cards
     * @param views card array
     * @param position current view to animate
     * @return Runnable
     */
    public Runnable getNextAnimator(final View[] views, final int position) {
        if(position >= views.length) {
            return null;
        }
        return () -> views[position]
                .animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .translationY(0.0f)
                .setInterpolator(new FastOutLinearInInterpolator())
                .setDuration(150)
                .withEndAction(getNextAnimator(views,position + 1))
                .start();
    }

    /**
     * Transition to {@link TimetableFragment}
     */
    public void onTimetableCardClick(){
        if(getActivity() == null){
            Log.wtf(CLASS_TAG, "Unable to get Activity");
            return;
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                .addToBackStack(TimetableFragment.CLASS_TAG)
                .replace(R.id.fragmentLayout, new TimetableFragment())
                .commit();
    }

    public void setViewModel(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public FragmentMainBinding getBinding() {
        return binding;
    }
}
