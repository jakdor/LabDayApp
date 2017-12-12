package com.jakdor.labday.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxSchedulersFacade;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Abstract base ViewModel
 */
public abstract class BaseViewModel extends AndroidViewModel {

    private ProjectRepository projectRepository;
    private RxSchedulersFacade rxSchedulersFacade;

    private CompositeDisposable disposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> loadingStatus = new MutableLiveData<>();
    private final MutableLiveData<RxResponse<AppData>> appData = new MutableLiveData<>();

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
    public void loadAppData(){
        if(projectRepository.getRepositoryState() == ProjectRepository.repositoryStates.READY){
            appData.setValue(projectRepository.getData());
        }
        else {
            disposable.add(projectRepository.getAppData()
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

    public String getProjectRepositoryHelloWorld(){
        return projectRepository.daggerHelloWorld();
    }
}
