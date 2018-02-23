package com.jakdor.labday.di;

import com.jakdor.labday.view.ui.EventFragment;
import com.jakdor.labday.view.ui.LoginFragment;
import com.jakdor.labday.view.ui.MainFragment;
import com.jakdor.labday.view.ui.SplashFragment;
import com.jakdor.labday.view.ui.TimetableFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * MainActivity fragment injection point
 */
@Module
public abstract class MainActivityFragmentBuilderModule {
    @ContributesAndroidInjector
    abstract MainFragment contributeMainFragment();

    @ContributesAndroidInjector
    abstract SplashFragment contributeSplashFragment();

    @ContributesAndroidInjector
    abstract LoginFragment contributeLoginFragment();

    @ContributesAndroidInjector
    abstract TimetableFragment contributeTimetableFragment();

    @ContributesAndroidInjector
    abstract EventFragment cotributeEventFragment();
}
