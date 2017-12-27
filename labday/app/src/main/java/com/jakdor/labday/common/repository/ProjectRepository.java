package com.jakdor.labday.common.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxSchedulersFacade;
import com.jakdor.labday.rx.RxStatus;

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

    private final String CLASS_TAG = "ProjectRepository";

    private NetworkManager networkManager;
    private RxSchedulersFacade rxSchedulersFacade;

    private RxResponse data;
    private String apiUpdateId;
    private boolean apiUpdateCurrent = false;
    private repositoryStates repositoryState = repositoryStates.INIT;

    private String accessToken;

    @Inject
    public ProjectRepository(NetworkManager networkManager, RxSchedulersFacade rxSchedulersFacade){
        this.networkManager = networkManager;
        this.rxSchedulersFacade = rxSchedulersFacade;
    }

    /**
     * Checks if logging in is required
     * @return boolean
     */
    public boolean isLoggedIn(){
        return false; //todo implement checking if token is saved
    }

    /**
     * Checks if update is necessary, then gets AppData from API or local db
     * @return {Single<RxResponse<AppData>>} appData wrapped with {@link RxResponse}
     */
    public Observable<RxResponse<AppData>> getUpdate(String apiUrl, Context context){
        networkManager.configAuth(apiUrl, "dummyToken");
        return Observable.create(e ->
                networkManager.getLastUpdate()
                .subscribeOn(rxSchedulersFacade.io())
                .observeOn(rxSchedulersFacade.ui())
                        .onErrorResumeNext(Observable.just("-1"))
                        .onExceptionResumeNext(Observable.just("-1"))
                .flatMap(s -> isLocalDataCurrent(apiUpdateId = s, context) ?
                        apiRequest(networkManager.getAppData()) : // load from local db //todo replace with local db access observable
                        apiRequest(networkManager.getAppData())) // get appData from api
                .subscribe(new Observer<RxResponse<AppData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RxResponse<AppData> appDataRxResponse) {
                        saveApiLastUpdateId(apiUpdateId, context);
                        e.onNext(appDataRxResponse);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if(apiUpdateCurrent) {
                            ProjectRepository.this.repositoryState = repositoryStates.NO_DB;
                            e.onNext(new RxResponse<>(RxStatus.NO_DB, null, throwable));
                        }
                        else {
                            ProjectRepository.this.repositoryState = repositoryStates.ERROR;
                            e.onNext(new RxResponse<>(RxStatus.ERROR, null, throwable));
                        }
                    }

                    @Override
                    public void onComplete() {
                        e.onComplete();
                    }
                })
        );
    }

    /**
     * Check if api update id matches local update id, saves
     * @param updateId API last update id
     * @param context required for accessing {@link SharedPreferences}
     * @return boolean
     */
    public boolean isLocalDataCurrent(String updateId, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        apiUpdateCurrent = sharedPreferences.getString(
                context.getString(R.string.pref_api_last_update_id), "0").equals(updateId);

        return apiUpdateCurrent;
    }

    /**
     * Save API last update id locally
     * @param updateId API last update id
     * @param context required for accessing {@link SharedPreferences}
     */
    public void saveApiLastUpdateId(String updateId, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(
                context.getString(R.string.pref_api_last_update_id), updateId).apply();
    }

    /**
     * Gets appData from api call
     * @return {Single<RxResponse<AppData>>} appData wrapped with {@link RxResponse}
     */
    public Observable<RxResponse<AppData>> getAppData(String apiUrl){
        networkManager.configAuth(apiUrl, "dummyToken");
        return apiRequest(networkManager.getAppData());
    }

    /**
     * ApiRequest chained observables
     * - first get Retrofit API response, then handle it - process received data, save locally
     * @param apiCall Observable Retrofit API call
     * @param <T> template to handle various API calls
     * @return {Observable<RxResponse<T>>}
     */
    public <T> Observable<RxResponse<T>> apiRequest(final Observable<T> apiCall) {
        return Observable.create(e ->
            apiCall.subscribeOn(rxSchedulersFacade.io())
                    .observeOn(rxSchedulersFacade.ui())
                    .subscribe(new Observer<T>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(T data) { //todo save data to local db
                            ProjectRepository.this.data = new RxResponse<>(RxStatus.SUCCESS, data, null);
                            ProjectRepository.this.repositoryState = repositoryStates.READY;
                            Log.i(CLASS_TAG, "API request success");
                            e.onNext(new RxResponse<>(RxStatus.SUCCESS, data, null));
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e(CLASS_TAG, "API request failed, " + throwable.toString());
                            ProjectRepository.this.repositoryState = repositoryStates.ERROR;
                            e.onNext(new RxResponse<>(RxStatus.ERROR, null, throwable));
                        }

                        @Override
                        public void onComplete() {
                            e.onComplete();
                        }
                    })
        );
    }

    public String daggerHelloWorld(){
        return "HelloStabo" + "\n" + networkManager.embeddedDaggerTest();
    }

    public RxResponse getData() {
        Log.i(CLASS_TAG, "repository data available locally");
        return data;
    }

    public repositoryStates getRepositoryState() {
        return repositoryState;
    }

    public enum repositoryStates{
        READY,
        INIT,
        ERROR,
        NO_DB
    }
}
