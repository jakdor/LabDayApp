package com.jakdor.labday.robolectric;

import java.util.Random;

abstract class TestUtils {

    /**
     * Crates random Strings for tests
     * @return String, length 0-50 chars A-z
     */
    static String randomString(){
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
}
