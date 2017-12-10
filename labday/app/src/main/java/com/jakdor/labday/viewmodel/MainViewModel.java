package com.jakdor.labday.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * ViewModel for {@link com.jakdor.labday.view.ui.MainFragment}
 */
public class MainViewModel extends AndroidViewModel {

    private ProjectRepository projectRepository;

    private CompositeDisposable disposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> loadingStatus = new MutableLiveData<>();
    private final MutableLiveData<RxResponse<List<AppData>>> appData = new MutableLiveData<>();

    @Inject
    public MainViewModel(@NonNull ProjectRepository projectRepository, @NonNull Application application) {
        super(application);
        this.projectRepository = projectRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    public void loadAppData(){
        disposable.add(projectRepository.getAppData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(disposable1 -> loadingStatus.setValue(true))
            .doAfterTerminate(() -> loadingStatus.setValue(false))
            .subscribe(
                    rxResponse -> {
                       if(rxResponse.status == RxStatus.SUCCESS){
                           appData.setValue(RxResponse.success(rxResponse.data));
                       }
                       else {
                           appData.setValue(RxResponse.error(rxResponse.error));
                       }
                    }
            ));
    }

    public MutableLiveData<RxResponse<List<AppData>>> getResponse() {
        return appData;
    }

    public MutableLiveData<Boolean> getLoadingStatus() {
        return loadingStatus;
    }

    public String getProjectRepositoryHelloWorld(){
        return projectRepository.daggerHelloWorld();
    }
}
