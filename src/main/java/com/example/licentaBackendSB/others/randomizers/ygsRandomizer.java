package com.example.licentaBackendSB.others.randomizers;

import java.util.ArrayList;
import java.util.List;

public class ygsRandomizer {

    public static String getRandomSeries() {
        final int minASCIIcodeForSeries = 65;
        final int maxASCIIcodeForSeries = 71;
        int randomASCII = (int) Math.floor(Math.random() * (maxASCIIcodeForSeries - minASCIIcodeForSeries + 1) + minASCIIcodeForSeries);

        return Character.toString((char) randomASCII);
    }

    public static String getRandomGroup() {
        return getHarcodedGroups().get((int) (Math.random() * 20));
    }

    private static List<String> getHarcodedGroups() {
        List<String> groups = new ArrayList<>();

        groups.add("411");
        groups.add("412");
        groups.add("413");
        groups.add("414");
        groups.add("415");

        groups.add("421");
        groups.add("422");
        groups.add("423");
        groups.add("424");
        groups.add("425");

        groups.add("431");
        groups.add("432");
        groups.add("433");
        groups.add("434");
        groups.add("435");

        groups.add("441");
        groups.add("442");
        groups.add("443");
        groups.add("444");
        groups.add("445");

        return groups;
    }
}
