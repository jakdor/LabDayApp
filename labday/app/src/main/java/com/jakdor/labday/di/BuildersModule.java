package com.jakdor.labday.di;

import com.jakdor.labday.view.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * app-wide Android dependencies injection point
 */
@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector(modules = MainActivityFragmentBuilderModule.class)
    abstract MainActivity contributeMainActivity();
}
