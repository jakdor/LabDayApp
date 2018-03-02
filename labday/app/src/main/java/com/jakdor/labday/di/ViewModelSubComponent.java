package com.jakdor.labday.di;

import com.jakdor.labday.viewmodel.EventViewModel;
import com.jakdor.labday.viewmodel.LoginViewModel;
import com.jakdor.labday.viewmodel.MainViewModel;
import com.jakdor.labday.viewmodel.MapViewModel;
import com.jakdor.labday.viewmodel.SplashViewModel;
import com.jakdor.labday.viewmodel.TimetableViewModel;

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
    LoginViewModel loginViewModel();
    TimetableViewModel timetableViewModel();
    EventViewModel eventViewModel();
    MapViewModel mapViewModel();
}