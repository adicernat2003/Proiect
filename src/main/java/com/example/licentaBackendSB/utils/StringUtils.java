package com.example.licentaBackendSB.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringUtils {
    public static String shuffleString(String string) {
        List<String> letters = Arrays.asList(string.split(""));
        Collections.shuffle(letters);
        StringBuilder shuffled = new StringBuilder();
        for (String letter : letters) {
            shuffled.append(letter);
        }
        return shuffled.toString();
    }
}
