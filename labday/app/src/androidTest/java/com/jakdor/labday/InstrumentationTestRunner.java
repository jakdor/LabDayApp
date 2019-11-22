package com.jakdor.labday;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.test.runner.AndroidJUnitRunner;

import io.appflate.restmock.RESTMockServerStarter;
import io.appflate.restmock.android.AndroidAssetsFileParser;
import io.appflate.restmock.android.AndroidLogger;

public class InstrumentationTestRunner extends AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        RESTMockServerStarter.startSync(new AndroidAssetsFileParser(getContext()),new AndroidLogger());
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestApp.class.getName(), context);
    }
}