package com.jakdor.labday.di;

import com.jakdor.labday.common.network.RetrofitBuilder;
import com.jakdor.labday.common.repository.NetworkManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Repository Dagger module
 */
@Module
public class RepositoryModule {

    @Singleton
    @Provides
    NetworkManager getNetworkManager(){
        return new NetworkManager(getRetrofitBuilder());
    }

    @Singleton
    @Provides
    RetrofitBuilder getRetrofitBuilder(){
        return new RetrofitBuilder();
    }
}
