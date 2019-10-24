package com.jakdor.labday.automatedUITests;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.InstrumentationRegistry;

import com.jakdor.labday.R;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;

/**
 * Suite for running Espresso UI tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({LoginEspressoTest.class})
public class EspressoTests {

    /**
     * Programmatically clear app saved state
     */
    @BeforeClass
    public static void beforeAll() {
        Context targetContext = InstrumentationRegistry.getTargetContext();

        File file = new File(targetContext.getFilesDir().getAbsolutePath() + "/lab");
        if(file.exists()){
            file.delete();
        }

        SharedPreferences sharedPreferences = targetContext.getSharedPreferences(
                targetContext.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(
                targetContext.getString(R.string.pref_api_last_update_id), "0").apply();
    }

}
