package com.jakdor.labday.di;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.jakdor.labday.App;

import com.jakdor.labday.common.repository.NetworkManager;
import com.jakdor.labday.viewmodel.ViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * App-wide dependency injection point
 */
@Module(subcomponents = ViewModelSubComponent.class)
public class AppModule {
    @Provides
    Context provideContext(App app){
        return app.getApplicationContext();
    }

    @Singleton
    @Provides
    ViewModelProvider.Factory provideViewModelFactory(ViewModelSubComponent.Builder viewModelBuilder){
        return new ViewModelFactory(viewModelBuilder.build());
    }

    //todo refactor
    @Singleton
    @Provides
    NetworkManager getNetworkManager(){
        return new NetworkManager();
    }
}
