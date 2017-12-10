package com.jakdor.labday.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.jakdor.labday.common.repository.ProjectRepository;

import javax.inject.Inject;

/**
 * ViewModel for {@link com.jakdor.labday.view.ui.MainFragment}
 */
public class MainViewModel extends AndroidViewModel {

    private ProjectRepository projectRepository;

    @Inject
    public MainViewModel(@NonNull ProjectRepository projectRepository, @NonNull Application application) {
        super(application);
        this.projectRepository = projectRepository;
    }

    public String getProjectRepositoryHelloWorld(){
        return projectRepository.daggerHelloWorld();
    }
}
