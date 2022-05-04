package com.example.licentaBackendSB.others.randomizers;

import java.nio.charset.StandardCharsets;

import static com.example.licentaBackendSB.managers.Manager.random;

public class nameRandomizer {

    //needed for Student object class
    public static String getAlphaNumericString(int n) {

        // length is bounded by 256 Character
        byte[] array = new byte[256];
        random.nextBytes(array);

        String randomString = new String(array, StandardCharsets.UTF_8);

        // Create a StringBuilder to store the result
        StringBuilder r = new StringBuilder();

        // remove all spacial char
        String AlphaNumericString = randomString.replaceAll("[^A-Za-z0-9]", "");

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < AlphaNumericString.length(); k++) {

            if (Character.isLetter(AlphaNumericString.charAt(k))
                    && (n > 0)
                    || Character.isDigit(AlphaNumericString.charAt(k))
                    && (n > 0)) {

                r.append(AlphaNumericString.charAt(k));
                n--;
            }
        }

        // return the resultant string
        return r.toString();
    }

}
