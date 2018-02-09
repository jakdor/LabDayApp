package com.jakdor.labday.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxSchedulersFacade;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Abstract base ViewModel
 */
public abstract class BaseViewModel extends AndroidViewModel {

    ProjectRepository projectRepository;
    RxSchedulersFacade rxSchedulersFacade;

    CompositeDisposable disposable = new CompositeDisposable();

    final MutableLiveData<Boolean> loadingStatus = new MutableLiveData<>();
    final MutableLiveData<RxResponse<AppData>> appData = new MutableLiveData<>();

    public BaseViewModel(@NonNull ProjectRepository projectRepository,
                         @NonNull Application application,
                         @NonNull RxSchedulersFacade rxSchedulersFacade) {
        super(application);
        this.projectRepository = projectRepository;
        this.rxSchedulersFacade = rxSchedulersFacade;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    /**
     * Setups observer for data from {@link ProjectRepository}
     */
    public void loadAppData(Context context){
        if(projectRepository.getRepositoryState() == ProjectRepository.repositoryStates.READY){
            appData.setValue(projectRepository.getData());
        }
        else {
            disposable.add(projectRepository.getAppData(LabService.MOCK_API_URL, context)
                    .subscribeOn(rxSchedulersFacade.io())
                    .observeOn(rxSchedulersFacade.ui())
                    .doOnSubscribe(disposable1 -> loadingStatus.setValue(true))
                    .doAfterTerminate(() -> loadingStatus.setValue(false))
                    .subscribe(appData::setValue));
        }
    }

    public MutableLiveData<RxResponse<AppData>> getResponse() {
        return appData;
    }

    public MutableLiveData<Boolean> getLoadingStatus() {
        return loadingStatus;
    }

}
