package com.jakdor.labday.di;

import androidx.lifecycle.ViewModelProvider;
import android.content.Context;

import com.jakdor.labday.App;
import com.jakdor.labday.rx.RxSchedulersFacade;
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
    public Context provideContext(App app){
        return app.getApplicationContext();
    }

    @Singleton
    @Provides
    public ViewModelProvider.Factory provideViewModelFactory(ViewModelSubComponent.Builder viewModelBuilder){
        return new ViewModelFactory(viewModelBuilder.build());
    }

    @Provides
    public RxSchedulersFacade provideRxSchedulersFacade(){
        return new RxSchedulersFacade();
    }
}
