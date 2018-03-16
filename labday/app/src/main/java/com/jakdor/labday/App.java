package com.jakdor.labday;

import android.app.Activity;
import android.app.Application;

import com.facebook.soloader.SoLoader;
import com.jakdor.labday.di.AppInjector;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * App entry point, dagger setup
 */
public class App extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            return;
        }
        LeakCanary.install(this);
        AppInjector.init(this);
        SoLoader.init(this, false);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
