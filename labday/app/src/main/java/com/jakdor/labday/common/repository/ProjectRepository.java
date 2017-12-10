package com.jakdor.labday.common.repository;

import android.arch.lifecycle.LiveData;

import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxSchedulersFacade;
import com.jakdor.labday.rx.RxStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Main app data repository handler - manages data sources
 */
@Singleton
public class ProjectRepository {

    private NetworkManager networkManager;
    private RxSchedulersFacade rxSchedulersFacade;

    private RxResponse data;
    private repositoryStates repositoryState = repositoryStates.INIT;

    @Inject
    public ProjectRepository(NetworkManager networkManager, RxSchedulersFacade rxSchedulersFacade){
        this.networkManager = networkManager;
        this.rxSchedulersFacade = rxSchedulersFacade;
    }

    /**
     * Gets appData from api call or from local db;
     * @return {Single<RxResponse<List<AppData>>>} appData wrapped with {@link RxResponse}
     */
    public Observable<RxResponse<List<AppData>>> getAppData(){
        return apiRequest(networkManager.getAppData());
    }

    /**
     * Embedded Observables - inner to get Retrofit API response, outer to handle it / process received data
     * (Not sure if correct approach... probably not, but it works!)
     * @param apiCall Observable Retrofit API call
     * @param <T> template to handle various API calls
     * @return Outer observable
     */
    public <T> Observable<RxResponse<T>> apiRequest(final Observable<T> apiCall) {
        return Observable.create(e -> {
            apiCall.subscribeOn(rxSchedulersFacade.io())
                    .observeOn(rxSchedulersFacade.ui())
                    .subscribe(new Observer<T>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(T data) {
                            ProjectRepository.this.data = new RxResponse<>(RxStatus.SUCCESS, data, null);
                            ProjectRepository.this.repositoryState = repositoryStates.READY;
                            e.onNext(new RxResponse<>(RxStatus.SUCCESS, data, null));
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            e.onNext(new RxResponse<>(RxStatus.ERROR, null, throwable));
                        }

                        @Override
                        public void onComplete() {
                            e.onComplete();
                        }
                    });
        });
    }

    public String daggerHelloWorld(){
        return "HelloStabo" + "\n" + networkManager.embeddedDaggerTest();
    }

    public RxResponse getData() {
        return data;
    }

    public repositoryStates getRepositoryState() {
        return repositoryState;
    }

    public enum repositoryStates{
        READY,
        INIT,
        ERROR
    }
}
