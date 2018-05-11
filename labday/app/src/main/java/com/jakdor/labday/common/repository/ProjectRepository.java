package com.jakdor.labday.common.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.KeyChain;
import com.jakdor.labday.R;
import com.jakdor.labday.common.localdb.LocalDbHandler;
import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.maps.MapPath;
import com.jakdor.labday.common.network.MapService;
import com.jakdor.labday.common.model.LastUpdate;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxSchedulersFacade;
import com.jakdor.labday.rx.RxStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Main app data repository handler - manages data sources
 */
@Singleton
public class ProjectRepository {

    private NetworkManager networkManager;
    private LocalDbHandler localDbHandler;
    private RxSchedulersFacade rxSchedulersFacade;

    private RxResponse<AppData> data;
    private String apiUpdateId;
    private boolean apiUpdateCurrent = false;
    private repositoryStates repositoryState = repositoryStates.INIT;

    private String accessToken;

    @Inject
    public ProjectRepository(NetworkManager networkManager,
                             LocalDbHandler localDbHandler,
                             RxSchedulersFacade rxSchedulersFacade){
        this.networkManager = networkManager;
        this.localDbHandler = localDbHandler;
        this.rxSchedulersFacade = rxSchedulersFacade;
    }

    /**
     * Login using basic auth method - login/password, save received access token, load appData
     * @return {Observable<RxResponse<AppData>>}
     */
    public Observable<RxResponse<AppData>> login(
            String apiUrl, Context context, String login, String password){

        if(!networkManager.checkNetworkStatus(context)){
            return Observable.just(RxResponse.noInternetNoDb(new Throwable("No internet service")));
        }

        networkManager.configAuth(apiUrl);
        return networkManager.getAccessToken(login, password)
                .subscribeOn(rxSchedulersFacade.io())
                .observeOn(rxSchedulersFacade.ui())
                .onErrorResumeNext(Observable.just(new AccessToken("-1")))
                .onExceptionResumeNext(Observable.just(new AccessToken("-1")))
                .doOnNext(accessTokenResponse ->
                        saveAccessToken(accessTokenResponse.getAccessToken(), context))
                .flatMap(accessTokenResponse -> getUpdate(apiUrl, context));
    }

    /**
     * Saves access token encrypted with Conceal
     * @param token access token retrieved after successful login
     * @param context required for SharedPreferences/Conceal
     */
    public void saveAccessToken(String token, Context context){

        if(token.equals("-1")){
            this.accessToken = token;
            Timber.e("bad access token");
            return;
        }

        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);

        try {
            byte[] tokenBytes = token.getBytes(Charset.forName("UTF-8"));
            byte[] encryptedToken = crypto.encrypt(tokenBytes, Entity.create("token"));

            FileOutputStream outputStream = context.openFileOutput("lab", Context.MODE_PRIVATE);
            outputStream.write(encryptedToken);
            outputStream.close();

            Timber.i("Successful login, access token saved");
        }
        catch (Exception e){
            Timber.wtf("unable to save access token");
        }

        this.accessToken = token;
    }

    /**
     * Loads access token and decrypts it with Conceal
     * @param context required for SharedPreferences/Conceal
     * @return boolean success/fail
     */
    public boolean loadAccessToken(Context context){
        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);

        try {
            byte[] encryptedToken = readFile(context,"lab");

            if(encryptedToken == null){
                Timber.e("Access token not found");
                return false;
            }
            else if(encryptedToken.length == 0){
                Timber.e("Access token not available");
                return false;
            }

            byte[] plainToken = crypto.decrypt(encryptedToken, Entity.create("token"));
            this.accessToken = new String(plainToken);
            Timber.i("Loaded and decrypted access token");
        }
        catch (Exception e){
            Timber.wtf("unable to decipher access token");
            return false;
        }

        return true;
    }

    /**
     * Checks if logging in is required
     * @return boolean
     */
    public boolean isLoggedIn(Context context){
        try {
            byte[] encryptedToken = readFile(context,"lab");
            if(encryptedToken == null){
                Timber.e("Access token not available");
                return false;
            }
            else if(encryptedToken.length == 0){
                Timber.e("Access token not available");
                return false;
            }
        }
        catch (Exception e){
            Timber.e("Unable to read access token, %s", e.toString());
            return false;
        }

        return true;
    }

    /**
     * Loads file to byte array
     * @param context used to obtain app fileDir
     * @param path file path
     * @return byte[] encrypted token
     */
    public byte[] readFile(Context context, String path) throws Exception{
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + path);
        FileInputStream inputStream = new FileInputStream(file);
        byte fileBytes[] = new byte[(int)file.length()];
        inputStream.read(fileBytes);
        inputStream.close();
        return fileBytes;
    }

    /**
     * Deletes saved access toke, mainly for test cleanup
     * @param context used to obtain app fileDir
     */
    public void deleteToken(Context context){
        File file = new File(context.getFilesDir().getAbsolutePath() + "/lab");
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * Checks if update is necessary, then gets AppData from API or local db
     * @return {Single<RxResponse<AppData>>} appData wrapped with {@link RxResponse}
     */
    public Observable<RxResponse<AppData>> getUpdate(String apiUrl, Context context){
        if(accessToken == null){
            if(!loadAccessToken(context)){
                repositoryState = repositoryStates.ERROR;
                return Observable.just(RxResponse.loginError(new Throwable("no access token")));
            }
        }
        else if(accessToken.equals("-1")){
            repositoryState = repositoryStates.ERROR;
            return Observable.just(RxResponse.loginError(new Throwable("bad access token")));
        }

        networkManager.configAuth(apiUrl, accessToken);
        networkManager.configAuth(apiUrl);
        return networkManager.getLastUpdate()
                .subscribeOn(rxSchedulersFacade.io())
                .observeOn(rxSchedulersFacade.ui())
                .onErrorResumeNext(Observable.just(new LastUpdate("-1")))
                .onExceptionResumeNext(Observable.just(new LastUpdate("-1")))
                .flatMap(s -> isLocalDataCurrent(apiUpdateId = s.getUpdatedAt(), context) ?
                        localDbHandler.getAppDataFromDb() : // load from local db
                        apiRequest(networkManager.getAppData())) // get appData from api
                .onErrorResumeNext(localDbHandler.getAppDataFromDb()) //last effort data retrieval
                .flatMap(appDataRxResponse -> { //last effort data retrieval from local db
                    if(appDataRxResponse.status == RxStatus.ERROR){
                        return localDbHandler.getAppDataFromDb();
                    }
                    else {
                        return Observable.just(appDataRxResponse);
                    }
                })
                .doOnNext(appDataRxResponse -> {
                    if(appDataRxResponse.status == RxStatus.SUCCESS) {
                        saveApiLastUpdateId(apiUpdateId, context);
                    }
                    else if(appDataRxResponse.status == RxStatus.SUCCESS_DB){
                        this.data = appDataRxResponse;
                        this.repositoryState = repositoryStates.READY;
                    }
                })
                .onErrorReturn(throwable -> {
                    if(apiUpdateCurrent) {
                        this.repositoryState = repositoryStates.NO_DB;
                        return RxResponse.noDb(throwable);
                    }
                    else {
                        this.repositoryState = repositoryStates.ERROR;
                        return RxResponse.error(throwable);
                    }
                });
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

        if(apiUpdateCurrent = sharedPreferences.getString(
                context.getString(R.string.pref_api_last_update_id), "0").equals(updateId)){
            Timber.i("Local data - up-to-date with API db");
            return true;
        }
        else {
            Timber.i("Local data - update required");
            return false;
        }
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
    public Observable<RxResponse<AppData>> getAppData(String apiUrl, Context context){
        if(accessToken == null){
            if(!loadAccessToken(context)){
                repositoryState = repositoryStates.ERROR;
                return Observable.just(RxResponse.loginError(new Throwable("no access token")));
            }
        }
        else if(accessToken.equals("-1")){
            repositoryState = repositoryStates.ERROR;
            return Observable.just(RxResponse.loginError(new Throwable("bad access token")));
        }

        networkManager.configAuth(apiUrl, accessToken);
        return apiRequest(networkManager.getAppData());
    }

    /**
     * ApiRequest chained observables
     * - first get Retrofit API response, then handle it - process received data, save locally
     * @param apiCall Observable Retrofit API call
     * @return {Observable<RxResponse<T>>}
     */
    public Observable<RxResponse<AppData>> apiRequest(final Observable<AppData> apiCall) {
        return apiCall.subscribeOn(rxSchedulersFacade.io())
                    .observeOn(rxSchedulersFacade.ui())
                    .doOnNext(appData -> {
                        this.data = RxResponse.success(appData);
                        this.repositoryState = repositoryStates.READY;
                        localDbHandler.pushAppDataToDb(appData); //save AppData to local db;
                        Timber.i("API request successful");
                    })
                    .map(RxResponse::success)
                    .onErrorReturn(throwable -> {
                        Timber.e("API request failed, %s", throwable.toString());
                        this.repositoryState = repositoryStates.ERROR;
                        return RxResponse.error(throwable);
                    });
    }

    //todo save last request in variable and return if same parameters - api calls limits preservation
    /**
     * Makes request to google map api with given parameters
     * @param origin origin lat&lang String
     * @param dest destination lat&lang String
     * @param apiKey google api key
     * @return {Observable<{@link RxResponse<MapPath>}}
     */
    public Observable<RxResponse<MapPath>> mapPathRequest(
            final String origin, final String dest, final String apiKey){
        networkManager.configMapService(MapService.GOOGLE_API);
        return networkManager.getMapPath(origin, dest, apiKey)
                .observeOn(rxSchedulersFacade.ui())
                .subscribeOn(rxSchedulersFacade.io())
                .map(RxResponse::success)
                .onErrorReturn(throwable -> {
                    Timber.e("Map path request failed, %s", throwable.toString());
                    return RxResponse.error(throwable);
                });
    }

    /**
     * Access to AppData if repository is in READY state
     * @return {RxResponse<AppData>}
     */
    public RxResponse<AppData> getData() {
        Timber.i("repository data available locally");
        return data;
    }

    public repositoryStates getRepositoryState() {
        return repositoryState;
    }

    /**
     * token access for test purpose
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * token access for test purpose
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public enum repositoryStates{
        READY,
        INIT,
        ERROR,
        NO_DB
    }
}
