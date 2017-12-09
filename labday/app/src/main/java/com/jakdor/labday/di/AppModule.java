package com.jakdor.labday.di;

import android.content.Context;
import com.jakdor.labday.App;
import com.jakdor.labday.common.NetworkManager;

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
    NetworkManager provideNetworkManager(){
        return new NetworkManager();
    }
}
