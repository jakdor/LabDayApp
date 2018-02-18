package com.jakdor.labday.robolectric;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * Reads file to string
     * @return String
     */
    static String readFile(String filePath) throws Exception{
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
    }
}
