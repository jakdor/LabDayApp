package com.jakdor.labday.common.repository;

import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Handles API calls
 */
public class NetworkManager {

    private LabService labService;

    @Inject
    public NetworkManager(RetrofitBuilder retrofitBuilder){
        labService = retrofitBuilder.getRetrofit().create(LabService.class);
    }

    /**
     * Main NetworkManager api call - get all appData bundled in single call
     * @return Observable with {@link AppData} in List
     */
    public Observable<List<AppData>> getAppData(){
        return labService.getProjectList("jakdor");
    }

    public String embeddedDaggerTest(){
        return "hello embeddedStabo";
    }
}
