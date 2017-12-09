package com.jakdor.labday.di;

import com.jakdor.labday.view.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * app wide dependencies injection point
 */
@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();
}
