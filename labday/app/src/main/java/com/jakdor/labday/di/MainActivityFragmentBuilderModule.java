package com.jakdor.labday.di;

import com.jakdor.labday.view.ui.MainFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * MainActivity fragment injection point
 */
@Module
public abstract class MainActivityFragmentBuilderModule {
    @ContributesAndroidInjector
    abstract MainFragment contributeMainFragment();
}
