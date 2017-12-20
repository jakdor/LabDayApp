package com.jakdor.labday.di;

import com.jakdor.labday.viewmodel.MainViewModel;
import com.jakdor.labday.viewmodel.SplashViewModel;

import dagger.Subcomponent;

/**
 * ViewModelFactory Dagger setup interface - App SubComponent
 * Called by {@link com.jakdor.labday.viewmodel.ViewModelFactory}
 */
@Subcomponent
public interface ViewModelSubComponent {

    @Subcomponent.Builder
    interface Builder {
        ViewModelSubComponent build();
    }

    MainViewModel mainViewModel();
    SplashViewModel splashViewModel();
}