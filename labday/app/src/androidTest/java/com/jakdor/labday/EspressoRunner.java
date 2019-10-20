package com.jakdor.labday;

import android.app.Application;
import android.content.Context;
import androidx.test.runner.AndroidJUnitRunner;

/**
 * Espresso test runner config
 */
public class EspressoRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        return super.newApplication(cl, App.class.getName(), context);
    }
}
