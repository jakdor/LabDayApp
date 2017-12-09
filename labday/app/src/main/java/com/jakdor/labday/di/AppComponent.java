package com.jakdor.labday.di;

import com.jakdor.labday.App;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 * Dagger main Android component setup interface
 */
@Singleton
@Component(modules = {
        AppModule.class,
        BuildersModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder application(App app);
        AppComponent build();
    }

    void inject(App app);
}
