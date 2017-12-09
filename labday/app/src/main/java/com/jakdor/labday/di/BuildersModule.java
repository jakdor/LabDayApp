package com.jakdor.labday.di;

import com.jakdor.labday.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * app sub-components' binding point
 */
@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();
}
