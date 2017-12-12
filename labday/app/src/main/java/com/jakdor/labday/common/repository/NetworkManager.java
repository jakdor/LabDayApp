package com.jakdor.labday.common.repository;

import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Handles API calls
 */
public class NetworkManager {

    private RetrofitBuilder retrofitBuilder;
    private LabService labService;
    private LabService loginLabService;

    @Inject
    public NetworkManager(RetrofitBuilder retrofitBuilder){
        this.retrofitBuilder = retrofitBuilder;
    }

    /**
     * no authorization config
     * @param apiUrl API base url
     */
    public void configAuth(String apiUrl){
        if(labService == null) {
            labService = retrofitBuilder.createService(apiUrl, LabService.class);
        }
    }

    /**
     * token authorization config
     * @param apiUrl API base url
     * @param token access token
     */
    public void configAuth(String apiUrl, String token){
        if(labService == null) {
            labService = retrofitBuilder.createService(apiUrl,
                    LabService.class, token);
        }
    }

    /**
     * login&password authorization config
     * @param apiUrl API base url
     * @param login one-time login
     * @param password one-time password
     */
    public void configAuth(String apiUrl, String login, String password){
        if(loginLabService == null) {
            loginLabService = retrofitBuilder.createService(apiUrl,
                    LabService.class, login, password);
        }
    }

    /**
     * Main NetworkManager api call - get all appData bundled in single call
     * @return Observable with {@link AppData} in List
     */
    public Observable<AppData> getAppData(){
        return labService.getAppData();
    }

    public String embeddedDaggerTest(){
        return "hello embeddedStabo";
    }
}
