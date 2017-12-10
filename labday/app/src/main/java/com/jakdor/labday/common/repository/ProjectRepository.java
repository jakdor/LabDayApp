package com.jakdor.labday.common.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Main app data repository handler - manages data sources
 */
@Singleton
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
