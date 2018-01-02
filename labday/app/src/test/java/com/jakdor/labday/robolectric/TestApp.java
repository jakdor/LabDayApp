package com.jakdor.labday.robolectric;

import com.jakdor.labday.App;
import com.jakdor.labday.di.DaggerAppComponent;

/**
 * Robolectric App override test class
 */
public class TestApp extends App {
    @Override
    public void onCreate() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }
}
