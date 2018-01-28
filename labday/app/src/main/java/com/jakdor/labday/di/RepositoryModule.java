package com.jakdor.labday.di;

import com.jakdor.labday.common.localdb.LocalDbHandler;
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
    public NetworkManager getNetworkManager(){
        return new NetworkManager(getRetrofitBuilder());
    }

    @Singleton
    @Provides
    public RetrofitBuilder getRetrofitBuilder(){
        return new RetrofitBuilder();
    }

    @Singleton
    @Provides
    public LocalDbHandler getLocalDBHandler() {
        return new LocalDbHandler();
    }
}
