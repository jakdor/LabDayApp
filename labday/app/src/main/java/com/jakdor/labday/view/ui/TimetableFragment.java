package com.jakdor.labday.view.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakdor.labday.R;
import com.jakdor.labday.databinding.FragmentTimetableBinding;
import com.jakdor.labday.di.InjectableFragment;
import com.jakdor.labday.viewmodel.TimetableViewModel;

import javax.inject.Inject;

public class TimetableFragment extends Fragment implements InjectableFragment {

    public static final String CLASS_TAG = "TimetableFragment";

    private TimetableViewModel viewModel;

    private FragmentTimetableBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_timetable, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(viewModel == null){
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(TimetableViewModel.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
                actionBar.setTitle(R.string.menu_title_timetable);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    public void setViewModel(TimetableViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public FragmentTimetableBinding getBinding() {
        return binding;
    }
}
