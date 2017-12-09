package com.jakdor.labday.di;

import android.content.Context;
import com.jakdor.labday.App;

import com.jakdor.labday.common.repository.NetworkManager;
import com.jakdor.labday.common.repository.ProjectRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * App-wide dependency injection point
 */
@Module
public class AppModule {

    @Provides
    Context provideContext(App app){
        return app.getApplicationContext();
    }

    @Singleton
    @Provides
    ProjectRepository provideProjectRepository(){
        return new ProjectRepository(new NetworkManager());
    }
}
