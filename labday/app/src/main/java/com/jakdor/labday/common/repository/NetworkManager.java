package com.jakdor.labday.common.repository;

import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;

import javax.inject.Inject;

/**
 * Handles API calls
 */
public class NetworkManager {

    private LabService labService;

    @Inject
    public NetworkManager(RetrofitBuilder retrofitBuilder){
        labService = retrofitBuilder.getRetrofit().create(LabService.class);
    }

    public String embeddedDaggerTest(){
        return "hello embeddedStabo";
    }
}
