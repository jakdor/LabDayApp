package com.jakdor.labday.UITests

import android.app.Activity
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.UiThreadTestRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.jakdor.labday.view.ui.TestActivity
import org.junit.Rule
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class UITestBase {

    @Rule @JvmField
    var thrown = ExpectedException.none()

    @Rule @JvmField
    var mActivityTestRule = ActivityTestRule(TestActivity::class.java)

    @Rule
    @JvmField
    var uiThreadTestRule = UiThreadTestRule()

    protected lateinit var testActivity: TestActivity

    @Throws(Throwable::class)
    protected fun getCurrentActivity(): Activity? {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val activity = arrayOfNulls<Activity>(1)
        uiThreadTestRule.runOnUiThread {
            val activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            activity[0] = Iterables.getOnlyElement(activities)
        }
        return activity[0]
    }

}