package com.jakdor.labday.di;

import android.app.Application;

import com.jakdor.labday.App;
import com.jakdor.labday.common.repository.NetworkManager;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Dagger main Android component setup interface
 */
@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        BuildersModule.class,
        RepositoryModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder{
        Builder appModule(AppModule appModule); //required by DaggerMock
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(App app);

    NetworkManager networkManager();
}
