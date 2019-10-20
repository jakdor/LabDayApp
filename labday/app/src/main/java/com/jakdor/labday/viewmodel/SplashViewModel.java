package com.jakdor.labday.viewmodel;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;

import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxSchedulersFacade;

import javax.inject.Inject;

/**
 * ViewModel for {@link com.jakdor.labday.view.ui.SplashFragment}
 */
public class SplashViewModel extends BaseViewModel {
    @Inject
    public SplashViewModel(@NonNull ProjectRepository projectRepository,
                         @NonNull Application application,
                         @NonNull RxSchedulersFacade rxSchedulersFacade) {
        super(projectRepository, application, rxSchedulersFacade);
    }

    /**
     * Check if update is available, setups observer for data from {@link ProjectRepository}
     */
    public void updateAppData(Context context){
        if(projectRepository.getRepositoryState() == ProjectRepository.repositoryStates.READY){
            appData.setValue(projectRepository.getData());
        }
        else {
            disposable.add(projectRepository.getUpdate(LabService.API_URL, context)
                    .subscribeOn(rxSchedulersFacade.io())
                    .observeOn(rxSchedulersFacade.ui())
                    .doOnSubscribe(disposable1 -> loadingStatus.setValue(true))
                    .doAfterTerminate(() -> loadingStatus.setValue(false))
                    .subscribe(appData::setValue));
        }
    }

    /**
     * Provides access to isLoggedIn
     * @return {projectRepository.isLoggedIn()}
     */
    public boolean isLoggedIn(Context context){
        return projectRepository.isLoggedIn(context);
    }
}
