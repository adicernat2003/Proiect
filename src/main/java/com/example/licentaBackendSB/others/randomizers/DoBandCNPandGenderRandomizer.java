package com.example.licentaBackendSB.others.randomizers;

import com.example.licentaBackendSB.enums.Gender;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static com.example.licentaBackendSB.managers.Manager.random;

@Component
public class DoBandCNPandGenderRandomizer {

    public String getDoBMaster(Integer anUniversitar) {
        int randomDay = this.getRandomDayOfBirth();
        int randomMonth = this.getRandomMonthOfBirth();
        int randomYearMaster = this.getRandomYearOfBirthForMaster(anUniversitar);
        return this.getDateOfBirth(randomDay, randomMonth, randomYearMaster);
    }

    public String getDoBLicenta(Integer anUniversitar) {
        int randomDay = this.getRandomDayOfBirth();
        int randomMonth = this.getRandomMonthOfBirth();
        int randomYearLicenta = this.getRandomYearOfBirthForLicenta(anUniversitar);
        return this.getDateOfBirth(randomDay, randomMonth, randomYearLicenta);
    }

    private String getDateOfBirth(int randomDay, int randomMonth, int randomYear) {
        Map<Integer, String> months = this.getMonths();
        return (randomDay < 10 ? "0" + randomDay : randomDay)
                + "." + months.get(randomMonth)
                + "." + randomYear;
    }

    private int getRandomYearOfBirthForMaster(Integer anUniversitar) {
        int firstYear = anUniversitar - 25;
        int lastYear = anUniversitar - 23;
        return random.nextInt(lastYear - firstYear) + firstYear;
    }

    private int getRandomYearOfBirthForLicenta(Integer anUniversitar) {
        int firstYear = anUniversitar - 23;
        int lastYear = anUniversitar - 18;
        return random.nextInt(lastYear - firstYear) + firstYear;
    }

    private int getRandomDayOfBirth() {
        int firstDayOfMonth = 1;
        int lastDayOfMonth = 31;
        return random.nextInt(lastDayOfMonth - firstDayOfMonth) + firstDayOfMonth;
    }

    private int getRandomMonthOfBirth() {
        int firstMonth = 1;
        int lastMonth = 12;
        return random.nextInt(lastMonth - firstMonth) + firstMonth;
    }

    public Gender getGender() {
        Gender[] genders = Gender.values();

        int startIndex = 1;
        int endIndex = 100;

        int randomGenderIndex = random.nextInt(endIndex - startIndex) + startIndex;

        return genders[randomGenderIndex % 2];
    }

    public String getCNP(String tmp, Gender gender) {
        Map<Integer, String> months = getMonths();
        Random rand = random;

        //Split zi de nastere dupa caracterul punct "."
        String day = tmp.split("\\.")[0];
        String month = tmp.split("\\.")[1];
        String year = tmp.split("\\.")[2];

        //Preluarea indexului lunii stiind valoarea din map si extragerea ultimelor 2 cifre din an
        Integer keyofMonth = getFirstKeyByValue(months, month);
        String last2DigitsFromYear = year.substring(2, 4);

        //In functie de sex si de an
        int genderIndicator;
        if (Gender.MASCULIN.equals(gender) && Integer.parseInt(year) < 2000)
            genderIndicator = 1;
        else if (Gender.MASCULIN.equals(gender) && Integer.parseInt(year) >= 2000)
            genderIndicator = 5;
        else if (Gender.FEMININ.equals(gender) && Integer.parseInt(year) < 2000)
            genderIndicator = 2;
        else
            genderIndicator = 6;

        //Generare random a codului de judet
        int startCountyCode = 1;
        int endCountyCode = 52;
        int randomCountyCode = 48;  //oricare din 47, 48, 49, 50
        while (!this.checkCountyCode(randomCountyCode)) {
            randomCountyCode = rand.nextInt(endCountyCode - startCountyCode) + startCountyCode;
        }

        //Generare random a ultimelor 4 cifre din cnp
        int startLetter = 0;
        int endLetter = 9;
        int randomFirstLetter = rand.nextInt(endLetter - startLetter) + startLetter;
        int randomSecondLetter = rand.nextInt(endLetter - startLetter) + startLetter;
        int randomThirdLetter = rand.nextInt(endLetter - startLetter) + startLetter;
        int randomFourthLetter = rand.nextInt(endLetter - startLetter) + startLetter;

        //Concatenarea rezultatului
        return genderIndicator + ""
                + last2DigitsFromYear + ""
                + (Integer.valueOf(10).compareTo(Objects.requireNonNull(keyofMonth)) > 0 ? "0" + keyofMonth : keyofMonth) + ""
                + day + ""
                + (randomCountyCode < 10 ? "0" + randomCountyCode : randomCountyCode) + ""
                + randomFirstLetter
                + randomSecondLetter
                + randomThirdLetter
                + randomFourthLetter;
    }

    public Boolean checkCountyCode(int tmp) {
        return tmp != 47 && tmp != 48 && tmp != 49 && tmp != 50;
    }

    public String splitDoBbyDot(String tmp) {
        Map<Integer, String> months = getMonths();

        String day = tmp.split("\\.")[0];
        String month = tmp.split("\\.")[1];
        String year = tmp.split("\\.")[2];
        Integer keyofMonth = getFirstKeyByValue(months, month);

        return day + (Integer.valueOf(10).compareTo(Objects.requireNonNull(keyofMonth)) > 0 ? "0" + keyofMonth : keyofMonth) + year;
    }

    public Map<Integer, String> getMonths() {
        Map<Integer, String> months = new HashMap<>();

        months.putIfAbsent(1, "Ianuarie");
        months.putIfAbsent(2, "Februarie");
        months.putIfAbsent(3, "Martie");
        months.putIfAbsent(4, "Aprilie");
        months.putIfAbsent(5, "Mai");
        months.putIfAbsent(6, "Iunie");
        months.putIfAbsent(7, "Iulie");
        months.putIfAbsent(8, "August");
        months.putIfAbsent(9, "Septembrie");
        months.putIfAbsent(10, "Octombrie");
        months.putIfAbsent(11, "Noiembrie");
        months.putIfAbsent(12, "Decembrie");

        return months;
    }

    public <T, E> T getFirstKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    //In situatia asta exista mai multe key cu aceeasi valoare
//    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
//        Set<T> keys = new HashSet<T>();
//        for (Map.Entry<T, E> entry : map.entrySet()) {
//            if (Objects.equals(value, entry.getValue())) {
//                keys.add(entry.getKey());
//            }
//        }
//        return keys;
//    }
}
