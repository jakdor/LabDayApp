package com.jakdor.labday.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import androidx.annotation.NonNull;

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
     * Check if data already available before requesting an update
     */
    public void loadAppData(Context context){
        if(projectRepository.getRepositoryState() == ProjectRepository.repositoryStates.READY){
            appData.setValue(projectRepository.getData());
            loadingStatus.setValue(false);
        }
        else {
            getUpdate(context);
        }
    }

    /**
     * Setups observer for data from {@link ProjectRepository}
     */
    public void getUpdate(Context context){
        disposable.add(projectRepository.getUpdate(LabService.API_URL, context)
                .subscribeOn(rxSchedulersFacade.io())
                .observeOn(rxSchedulersFacade.ui())
                .doOnSubscribe(disposable1 -> loadingStatus.setValue(true))
                .doAfterNext(appDataRxResponse -> loadingStatus.setValue(false))
                .doAfterTerminate(() -> loadingStatus.setValue(false))
                .subscribe(appData::setValue));
    }

    public MutableLiveData<RxResponse<AppData>> getResponse() {
        return appData;
    }

    public MutableLiveData<Boolean> getLoadingStatus() {
        return loadingStatus;
    }

}
