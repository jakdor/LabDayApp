package com.jakdor.labday.view.ui;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.Path;
import com.jakdor.labday.databinding.FragmentTimetableBinding;
import com.jakdor.labday.di.InjectableFragment;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;
import com.jakdor.labday.view.adapter.TimetableAdapter;
import com.jakdor.labday.utils.GlideApp;
import com.jakdor.labday.viewmodel.TimetableViewModel;

import javax.inject.Inject;

import timber.log.Timber;

public class TimetableFragment extends Fragment implements InjectableFragment {

    public static final String CLASS_TAG = "TimetableFragment";

    private TimetableViewModel viewModel;
    private int activePathId;
    private boolean blockWhileLoading = false;

    private FragmentTimetableBinding binding;

    private boolean testMode = false;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public static TimetableFragment newInstance(int pathId) {
        TimetableFragment timetableFragment = new TimetableFragment();

        Bundle args = new Bundle();
        args.putInt("path", pathId);
        timetableFragment.setArguments(args);

        return timetableFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            activePathId = getArguments().getInt("path");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_timetable, container, false);

        binding.getRoot().measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);

        //force update check on swipe-to-refresh
        binding.swiperefreshTimetable.setOnRefreshListener(() -> {
            if(!blockWhileLoading)
                viewModel.getUpdate(getContext());
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(viewModel == null){
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(TimetableViewModel.class);

            observeAppData();
            observeLoadingStatus();
        }

        viewModel.loadAppData(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(testMode) return;
        if(getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
                actionBar.setTitle(R.string.menu_title_timetable);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(testMode) return;
        if(getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    public void observeAppData(){
        viewModel.getResponse().observe(this, this::processResponse);
    }

    public void observeLoadingStatus(){
        viewModel.getLoadingStatus().observe(this, this::handleLoadingStatus);
    }

    /**
     * Set path name in timetable card
     */
    public void processResponse(RxResponse<AppData> response) {
        binding.swiperefreshTimetable.setRefreshing(false); //stop swipe to refresh anim

        if(response.status == RxStatus.SUCCESS || response.status == RxStatus.SUCCESS_DB){
            loadRecyclerView(response.data, activePathId);
        }
        else {
            if(response.error != null) {
                Timber.e(response.error.toString());
            }
        }
    }

    public void handleLoadingStatus(Boolean status){
        blockWhileLoading = status;
    }

    /**
     * Get window height for auto scaling in RecyclerView
     * @return int window height or 0 if unable to get height
     */
    public int getHeight(){
        int height = 0;

        if(getActivity() != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
        }

        return height;
    }

    /**
     * Load RecyclerView, and path info to bar
     * @param appData from repository
     * @param activePathId id of active Path
     */
    private void loadRecyclerView(AppData appData, int activePathId){
        for (Path path : appData.getPaths()) {
            if(path.getId() == activePathId){
                binding.setTitle(path.getInfo());
                break;
            }
        }

        RecyclerView recyclerView = binding.timetableRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        TimetableAdapter timetableAdapter
                = new TimetableAdapter(this, appData, activePathId,
                GlideApp.with(this), getHeight());
        recyclerView.setAdapter(timetableAdapter);
        recyclerView.invalidate();
    }

    public void setViewModel(TimetableViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public FragmentTimetableBinding getBinding() {
        return binding;
    }

    public boolean isBlockWhileLoading() {
        return blockWhileLoading;
    }

    public void setTestMode() {
        this.testMode = true;
    }
}
