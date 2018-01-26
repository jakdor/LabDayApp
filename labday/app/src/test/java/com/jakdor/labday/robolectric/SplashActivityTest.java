package com.jakdor.labday.robolectric;

import android.content.Intent;
import android.view.View;

import com.jakdor.labday.TestApp;
import com.jakdor.labday.view.ui.MainActivity;
import com.jakdor.labday.view.ui.SplashActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class SplashActivityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ActivityController activityController;
    private SplashActivity splashActivity;

    @Before
    public void setUp() throws Exception{
        activityController = Robolectric.buildActivity(SplashActivity.class).create().start();
        splashActivity = (SplashActivity)activityController.get();
    }

    @Test
    public void viewTest() throws Exception {
        activityController.visible();
        View view = splashActivity.getWindow().getDecorView();
        Assert.assertNotNull(view);
    }

    @Test
    public void intentTest() throws Exception {
        Intent expectedIntent = new Intent(splashActivity, MainActivity.class);
        expectedIntent.setFlags(expectedIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();

        Assert.assertTrue(expectedIntent.filterEquals(actualIntent));
    }

    @Test
    public void finishTest() throws Exception {
        activityController.visible();
        Assert.assertTrue(splashActivity.isFinishing());
    }

}
