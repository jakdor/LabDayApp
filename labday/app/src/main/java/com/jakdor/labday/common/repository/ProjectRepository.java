package com.jakdor.labday.common.repository;

import javax.inject.Inject;

/**
 * Main app data repository handler - manages data sources
 */
public class ProjectRepository {

    private NetworkManager networkManager;

    @Inject
    public ProjectRepository(NetworkManager networkManager){
        this.networkManager = networkManager;
    }

    public String daggerHelloWorld(){
        return "HelloStabo" + "\n" + networkManager.embeddedDaggerTest();
    }
}
