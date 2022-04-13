package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.others.randomizers.CountyManager;
import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
import com.example.licentaBackendSB.others.randomizers.nameRandomizer;
import com.example.licentaBackendSB.others.randomizers.ygsRandomizer;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class StudentsLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;

    public static List<Student> studentsDB;

    @Override
    public void run(String... args) {
        log.info("Loading data from StudentLoader...");

        studentRepository.saveAll(studentsDB);
    }

    public static List<Student> hardcodeStudents() {
        List<Student> hardcodedListOfStudents = new ArrayList<>();

        //manual harcode to test search query => check StudentRepository
        hardcodedListOfStudents.add(
                Student.builder()
                        .nume("Cernat")
                        .prenume("Adrian")
                        .grupa("442")
                        .serie("B")
                        .an(4)
                        .medie(10D)
                        .myToken(shuffleString("cernat" + "adrian"))
                        .zi_de_nastere("23.Ianuarie.1999")
                        .cnp("1990123410036")
                        .genSexual("Masculin")
                        .judet("Bucuresti")
                        .friendToken("null")
                        .camin_preferat("null")
                        .flagCazSpecial("Nu")
                        .anUniversitar(2021)
                        .build());

        for (long i = 1; i < 10; i++) {
            String group = ygsRandomizer.getRandomGroup();
            String series = ygsRandomizer.getRandomSeries();

            int year = Character.getNumericValue(group.charAt(1));

            String randomNume = nameRandomizer.getAlphaNumericString(5);
            String randomPrenume = nameRandomizer.getAlphaNumericString(5);

            String randomDoB = DoBandCNPandGenderRandomizer.getDoB();
            String randomGender = DoBandCNPandGenderRandomizer.getGender();
            String randomCNP = DoBandCNPandGenderRandomizer.getCNP(randomDoB, randomGender);

            String countyCode = randomCNP.substring(7, 9);
            String randomCounty = CountyManager.getCountyFromTwoDigitCode(countyCode);

            hardcodedListOfStudents.add(Student.builder()
                    .nume(randomNume)
                    .prenume(randomPrenume)
                    .grupa(group)
                    .serie(series)
                    .an(year)
                    .medie((1D + (10D - 1D) * new Random().nextDouble()))
                    .myToken(shuffleString(randomNume + randomPrenume))
                    .zi_de_nastere(randomDoB)
                    .cnp(randomCNP)
                    .genSexual(randomGender)
                    .judet(randomCounty)
                    .friendToken("null")
                    .camin_preferat("null")
                    .flagCazSpecial("Nu")
                    .anUniversitar(2021)
                    .build());
        }
        return hardcodedListOfStudents;
    }

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
