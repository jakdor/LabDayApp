package com.jakdor.labday.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxSchedulersFacade;

import javax.inject.Inject;

/**
 * ViewModel for {@link com.jakdor.labday.view.ui.MainFragment}
 */
public class MainViewModel extends BaseViewModel {
    @Inject
    public MainViewModel(@NonNull ProjectRepository projectRepository,
                         @NonNull Application application,
                         @NonNull RxSchedulersFacade rxSchedulersFacade) {
        super(projectRepository, application, rxSchedulersFacade);
    }
}
