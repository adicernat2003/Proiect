package com.example.licentaBackendSB.others.randomizers;

import java.nio.charset.StandardCharsets;

import static com.example.licentaBackendSB.managers.Manager.random;

public class nameRandomizer {

    public static String getAlphaNumericString(int n) {

        byte[] array = new byte[256];
        random.nextBytes(array);

        String randomString = new String(array, StandardCharsets.UTF_8);

        StringBuilder r = new StringBuilder();

        String AlphaNumericString = randomString.replaceAll("[^A-Za-z0-9]", "");

        for (int k = 0; k < AlphaNumericString.length(); k++) {

            if (Character.isLetter(AlphaNumericString.charAt(k))
                    && (n > 0)
                    || Character.isDigit(AlphaNumericString.charAt(k))
                    && (n > 0)) {

                r.append(AlphaNumericString.charAt(k));
                n--;
            }
        }

        return r.toString();
    }

}
