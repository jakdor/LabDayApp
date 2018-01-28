package com.jakdor.labday.androidjunit;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Instrumentation test helper functions
 */
class TestUtils {

    /**
     * Read asset file to String
     * @param context required to access assets in apk
     * @param path file path
     * @return file loaded to String
     * @throws Exception file parsing / assets access Exceptions
     */
    static String readAssetFile(Context context, String path) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        InputStream json = context.getAssets().open(path);
        BufferedReader bufferedReader
                = new BufferedReader(new InputStreamReader(json, "UTF-8"));

        String str;
        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str);
        }

        bufferedReader.close();

        return new String(stringBuilder);
    }
}
