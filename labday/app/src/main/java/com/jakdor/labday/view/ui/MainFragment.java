package com.jakdor.labday.view.ui;

import android.annotation.SuppressLint;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.Path;
import com.jakdor.labday.databinding.FragmentMainBinding;
import com.jakdor.labday.di.InjectableFragment;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;
import com.jakdor.labday.view.utils.GlideApp;
import com.jakdor.labday.viewmodel.MainViewModel;

import javax.inject.Inject;

/**
 * MainActivity landing fragment
 */
public class MainFragment extends Fragment implements InjectableFragment {

    private final String CLASS_TAG = "MainFragment";

    private FragmentMainBinding binding;
    private FragmentManager fragmentManager = getFragmentManager();
    private Handler animationHandler = new Handler();
    private Path activePath;

    private MainViewModel viewModel;
    private boolean testMode;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        GlideApp.with(this)
                .load(R.drawable.lab_day_logo_full)
                .into(binding.menuLogo);

        if(getActivity() == null){
            Log.wtf(CLASS_TAG, "Unable to get Activity");
        }
        else {
            fragmentManager = getActivity().getSupportFragmentManager();
        }

        animateMenuItems();

        binding.menuTimetable.menuCard.setOnClickListener(view -> onTimetableCardClick());
        binding.menuMap.menuCard.setOnClickListener(view -> onPlacesCardClick());
        binding.menuMedia.menuCard.setOnClickListener(view -> onMediaCardClick());

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

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        if(testMode) return;
        if(getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setShowHideAnimationEnabled(false);
            }
        }
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
                    if(path.getActive()){
                        activePath = path;
                        binding.setPath(activePath.getName());
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
        fragmentManager.beginTransaction()
                .addToBackStack(TimetableFragment.CLASS_TAG)
                .replace(R.id.fragmentLayout, TimetableFragment.newInstance(activePath.getId()))
                .commit();
    }

    /**
     * Transition to {@link PlacesFragment}
     */
    public void onPlacesCardClick(){
        fragmentManager.beginTransaction()
                .addToBackStack(PlacesFragment.CLASS_TAG)
                .replace(R.id.fragmentLayout, new PlacesFragment())
                .commit();
    }

    /**
     * Transition to {@link MediaFragment}
     */
    public void onMediaCardClick(){
        fragmentManager.beginTransaction()
                .addToBackStack(MediaFragment.CLASS_TAG)
                .replace(R.id.fragmentLayout, new MediaFragment())
                .commit();
    }

    public void setViewModel(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public FragmentMainBinding getBinding() {
        return binding;
    }

    public void setTestMode() {
        this.testMode = true;
    }
}
