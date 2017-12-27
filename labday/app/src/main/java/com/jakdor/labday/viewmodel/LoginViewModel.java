package com.jakdor.labday.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxSchedulersFacade;

import javax.inject.Inject;

/**
 * ViewModel for
 */
public class LoginViewModel extends BaseViewModel {
    @Inject
    public LoginViewModel(@NonNull ProjectRepository projectRepository,
                          @NonNull Application application,
                          @NonNull RxSchedulersFacade rxSchedulersFacade) {
        super(projectRepository, application, rxSchedulersFacade);
    }
}
