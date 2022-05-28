package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.enums.Master;
import com.example.licentaBackendSB.managers.Manager;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.licentaBackendSB.constants.Constants.DEFAULT_YEAR;
import static com.example.licentaBackendSB.managers.Manager.random;
import static com.example.licentaBackendSB.model.entities.Student.endIndexing;
import static com.example.licentaBackendSB.model.entities.Student.startIndexing;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class StudentsLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final DoBandCNPandGenderRandomizer doBandCNPandGenderRandomizer;
    private final CountyManager countyManager;
    private final Manager manager;

    public static List<Student> studentsDB;

    @Override
    public void run(String... args) {
        log.info("Loading data from StudentLoader...");

        studentRepository.saveAll(studentsDB);
    }

    public List<Student> hardcodeStudents() {
        List<Student> hardcodedListOfStudents = new ArrayList<>();
        long numberOfTotalStudentsToBeAdded = endIndexing - startIndexing + 1;
        long numberOfMasteranziToBeAdded = numberOfTotalStudentsToBeAdded / 3;
        long numberOfStudentsLaLicentaToBeAdded = numberOfTotalStudentsToBeAdded - numberOfMasteranziToBeAdded;

        hardcodedListOfStudents = this.buildStudentsLaLicenta(hardcodedListOfStudents, numberOfStudentsLaLicentaToBeAdded);
        hardcodedListOfStudents = this.buildMasteranzi(hardcodedListOfStudents, numberOfMasteranziToBeAdded);

        return hardcodedListOfStudents;
    }

    private List<Student> buildStudentsLaLicenta(List<Student> hardcodedListOfStudents, long numberOfStudentsLaLicentaToBeAdded) {
        for (long i = 0; i < numberOfStudentsLaLicentaToBeAdded; i++) {
            hardcodedListOfStudents.add(this.createStudentLicenta());
        }
        return hardcodedListOfStudents;
    }

    private List<Student> buildMasteranzi(List<Student> hardcodedListOfStudents, long numberOfStudentsLaLicentaToBeAdded) {
        for (long i = 0; i < numberOfStudentsLaLicentaToBeAdded; i++) {
            hardcodedListOfStudents.add(this.createStudentMaster());
        }
        return hardcodedListOfStudents;
    }

    private Student createStudentLicenta() {
        String group = ygsRandomizer.getRandomGroup();
        int year = Character.getNumericValue(group.charAt(1));

        String randomNume = nameRandomizer.getAlphaNumericString(5);
        String randomPrenume = nameRandomizer.getAlphaNumericString(5);

        String randomDoB = doBandCNPandGenderRandomizer.getDoBLicenta(DEFAULT_YEAR);
        Gender randomGender = doBandCNPandGenderRandomizer.getGender();
        String randomCNP = doBandCNPandGenderRandomizer.getCNP(randomDoB, randomGender);


        return new Student().toBuilder()
                .nume(randomNume)
                .prenume(randomPrenume)
                .grupa(group)
                .serie(ygsRandomizer.getRandomSeries())
                .an(year)
                .medie((1D + (10D - 1D) * random.nextDouble()))
                .zi_de_nastere(randomDoB)
                .cnp(randomCNP)
                .genSexual(randomGender)
                .judet(countyManager.getCountyFromTwoDigitCode(randomCNP.substring(7, 9)))
                .isCazSpecial(Boolean.FALSE)
                .anUniversitar(2021)
                .isMasterand(Boolean.FALSE)
                .prioritate(manager.getRandomPrioritate())
                .build();
    }

    private Student createStudentMaster() {
        String randomNume = nameRandomizer.getAlphaNumericString(5);
        String randomPrenume = nameRandomizer.getAlphaNumericString(5);

        String randomDoB = doBandCNPandGenderRandomizer.getDoBMaster(DEFAULT_YEAR);
        Gender randomGender = doBandCNPandGenderRandomizer.getGender();
        String randomCNP = doBandCNPandGenderRandomizer.getCNP(randomDoB, randomGender);

        return new Student().toBuilder()
                .nume(randomNume)
                .prenume(randomPrenume)
                .an(this.getRandomYearForMaster())
                .medie((1D + (10D - 1D) * random.nextDouble()))
                .zi_de_nastere(randomDoB)
                .cnp(randomCNP)
                .genSexual(randomGender)
                .judet(countyManager.getCountyFromTwoDigitCode(randomCNP.substring(7, 9)))
                .isCazSpecial(Boolean.FALSE)
                .anUniversitar(2021)
                .isMasterand(Boolean.TRUE)
                .master(this.getRandomMaster())
                .prioritate(manager.getRandomPrioritate())
                .build();
    }

    private int getRandomYearForMaster() {
        return Boolean.TRUE.equals(random.nextBoolean()) ? 2 : 1;
    }

    public Master getRandomMaster() {
        List<Master> masters = Arrays.asList(Master.values());
        return masters.get(random.nextInt(masters.size()));
    }
}
