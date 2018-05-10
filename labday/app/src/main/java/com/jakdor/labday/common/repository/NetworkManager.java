package com.jakdor.labday.common.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.maps.MapPath;
import com.jakdor.labday.common.model.LastUpdate;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.MapService;
import com.jakdor.labday.common.network.RetrofitBuilder;

import javax.inject.Inject;

import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Handles API calls
 */
public class NetworkManager {

    private RetrofitBuilder retrofitBuilder;
    private LabService labService;
    private MapService mapService;
    private LabService updateLabService;

    @Inject
    public NetworkManager(RetrofitBuilder retrofitBuilder){
        this.retrofitBuilder = retrofitBuilder;
    }

    /**
     * Check network status
     * @param context required to retrieve ConnectivityService
     * @return boolean - network status
     */
    public boolean checkNetworkStatus(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager == null){
            Timber.wtf("Internet status: failed to get status!");
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null){
            Timber.e( "Internet status: no service!");
            return false;
        }
        else {
            Timber.i("Internet status: OK");
            return true;
        }
    }

    /**
     * no authorization config
     * @param apiUrl API base url
     */
    public void configAuth(String apiUrl){
        if(updateLabService == null) {
            updateLabService = retrofitBuilder.createService(apiUrl, LabService.class);
        }
    }

    /**
     * token authorization config
     * @param apiUrl API base url
     * @param token access token
     */
    public void configAuth(String apiUrl, String token){
        if(labService == null) {
            labService = retrofitBuilder.createService(apiUrl, LabService.class, token);
        }
    }

    /**
     * Config service for Google map API
     * @param apiUrl Google API
     */
    public void configMapService(String apiUrl){
        if(mapService == null){
            mapService = retrofitBuilder.createService(apiUrl, MapService.class);
        }
    }

    /**
     * Main NetworkManager api call - get all appData bundled in single call
     * @return Observable with {@link AppData}
     */
    public Observable<AppData> getAppData(){
        return labService.getAppData();
    }

    /**
     * Last update api call - backend db update id/time in seconds from unix epoch;
     * @return Observable {@link LastUpdate} with update unique id
     */
    public Observable<LastUpdate> getLastUpdate(){
        return updateLabService.getLastUpdate();
    }

    /**
     * Login api call - returns access token after successful login
     * @return Observable {@link AccessToken}
     */
    public Observable<AccessToken> getAccessToken(String user, String password){
        return updateLabService.getAccessToken(user, password);
    }

    /**
     * Makes call to google map api to plot path between points
     * travel mode parameter - walking
     * @param origin origin lat&lang String
     * @param dest destination lat&lang String
     * @param apiKey google api key String
     * @return Observable {@link MapPath}
     */
    public Observable<MapPath> getMapPath(String origin, String dest, String apiKey){
        return mapService.getMapPath(origin, dest, apiKey, "walking");
    }

    /**
     * @return {@link LabService} instance for checking in tests
     */
    public LabService getLabService() {
        return labService;
    }

    /**
     * @return {@link LabService} instance with authorization header for checking in tests
     */
    public LabService getLoginLabService() {
        return updateLabService;
    }
}
