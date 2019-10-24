package com.jakdor.labday;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public abstract class TestUtils {

    /**
     * Crates random Strings for tests
     * @return String, length 0-50 chars A-z
     */
    public static String randomString(){
        int leftLimit = 'A';
        int rightLimit = 'z';
        Random random = new Random();
        int targetStringLength = random.nextInt(50);
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    /**
     * Read asset file to String
     * @param context required to access assets in apk
     * @param path file path
     * @return file loaded to String
     * @throws Exception file parsing / assets access Exceptions
     */
    public static String readAssetFile(Context context, String path) throws Exception{
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
