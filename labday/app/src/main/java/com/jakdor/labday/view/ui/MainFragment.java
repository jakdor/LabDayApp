package com.jakdor.labday.view.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.databinding.FragmentMainBinding;
import com.jakdor.labday.di.InjectableFragment;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.viewmodel.MainViewModel;

import java.util.List;

import javax.inject.Inject;

/**
 * MainActivity landing fragment
 */
public class MainFragment extends Fragment implements InjectableFragment {

    private final String CLASS_TAG = "MainFragment";

    private FragmentMainBinding binding;

    private MainViewModel viewModel;

    @Inject

    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MainViewModel.class);

        //quick architecture test
        binding.setHelloWorld(viewModel.getProjectRepositoryHelloWorld());

        observeAppData();

        viewModel.loadAppData();
    }

    public void observeAppData(){
        viewModel.getResponse().observe(this, this::processResponse);
    }

    private void processResponse(RxResponse<List<AppData>> response) {
        switch (response.status) {
            case SUCCESS:
                binding.setHelloWorld(response.data.get(0).full_name);
                break;

            case ERROR:
                Log.e(CLASS_TAG, response.error.toString());
                binding.setHelloWorld(viewModel.getProjectRepositoryHelloWorld());
                break;
        }
    }
}
