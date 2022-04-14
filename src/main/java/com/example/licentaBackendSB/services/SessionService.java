package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.enums.AnDeStudiu;
import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.loaders.StudentsLoader;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.randomizers.CountyManager;
import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
import com.example.licentaBackendSB.others.randomizers.nameRandomizer;
import com.example.licentaBackendSB.others.randomizers.ygsRandomizer;
import com.example.licentaBackendSB.repositories.CaminRepository;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static com.example.licentaBackendSB.enums.AnDeStudiu.*;
import static com.example.licentaBackendSB.utils.StringUtils.shuffleString;
import static org.hibernate.type.IntegerType.ZERO;

@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class SessionService {

    private final CaminRepository caminRepository;
    private final StudentRepository studentRepository;
    private final StudentAccountRepository studentAccountRepository;
    private final DoBandCNPandGenderRandomizer doBandCNPandGenderRandomizer;
    private final CountyManager countyManager;
    private final StudentsLoader studentsLoader;
    private final StudentAccountService studentAccountService;

    public List<Student> createNewSession(Integer anUniversitar) {
        this.getCamineOfAnUniversitar(anUniversitar);
        return this.getStudentsOfAnUniversitar(anUniversitar);
    }

    private List<Student> getStudentsOfAnUniversitar(Integer anUniversitar) {
        Integer numberOfStudentsForAnUniversitar = studentRepository.countAllByAnUniversitar(anUniversitar);
        if (ZERO.compareTo(numberOfStudentsForAnUniversitar) == 0) {
            return this.createStudentsOfAnUniversitar(anUniversitar);
        } else {
            return studentRepository.findAllByAnUniversitar(anUniversitar);
        }
    }

    private List<Student> createStudentsOfAnUniversitar(Integer anUniversitar) {
        List<Student> previousStudentOfAnUniversitar = studentRepository.findAllByAnUniversitar(anUniversitar - 1);
        List<Student> previousStudentsInFirstAndThirdYearOfLicenta = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar);
        List<Student> previousStudentsInSecondYearOfLicenta = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar, TWO, false);
        List<Student> previousStudentsInFourthYearOfLicenta = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar, FOUR, false);

        List<Student> previousStudentsInFirstYearOfMaster = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar, ONE, true);
        List<Student> previousStudentsInSecondYearOfMaster = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar, TWO, true);

        int totalNumberOfStudents = previousStudentOfAnUniversitar.size();
        int totalNumberOfStudentsAfterDeletingStudentsFromSecondYearOfMaster = totalNumberOfStudents - previousStudentsInSecondYearOfMaster.size();
        int difference = totalNumberOfStudents - totalNumberOfStudentsAfterDeletingStudentsFromSecondYearOfMaster;
        int previousNumberOfStudentsInFourthYearOfLicenta = previousStudentsInFourthYearOfLicenta.size();
        int totalNumberOfStudentsCareAplicaPentruMaster = this.getRandomNumberOfStudentiCareAplicaPentruMaster(previousNumberOfStudentsInFourthYearOfLicenta);

        int totalNumberOfNewStudentsInFirstYearOfLicenta = this.getTotalNumberOfStudentsInFirstYearOfLicenta(previousNumberOfStudentsInFourthYearOfLicenta,
                totalNumberOfStudentsCareAplicaPentruMaster, difference);

        List<Student> newStudentsInFirstYearOfLicenta = this.createListOfNewStudentsInFirstYearOfLicenta(totalNumberOfNewStudentsInFirstYearOfLicenta, anUniversitar);
        List<Student> newStudentsInSecondAndFourthYearOfLicenta = previousStudentsInFirstAndThirdYearOfLicenta.stream()
                .map(student -> this.buildNewStudentInSecondOrFourthYear(student, anUniversitar))
                .toList();
        List<Student> newStudentsInThirdYearOfLicenta = previousStudentsInSecondYearOfLicenta.stream()
                .map(student -> this.buildNewStudentInThirdYear(student, anUniversitar))
                .toList();

        List<Student> newStudentInFirstYearOfMaster = this.createListOfNewStudentsInFirstYearOfMaster(previousStudentsInFourthYearOfLicenta,
                totalNumberOfStudentsCareAplicaPentruMaster, anUniversitar);
        List<Student> newStudentInSecondYearOfMaster = previousStudentsInFirstYearOfMaster.stream()
                .map(student -> this.buildNewStudentInSecondYearOfMaster(student, anUniversitar))
                .toList();

        List<Student> studentsToBeAddedInDB = this.getListOfStudentsToBeAddedInDB(newStudentsInFirstYearOfLicenta, newStudentsInSecondAndFourthYearOfLicenta,
                newStudentsInThirdYearOfLicenta, newStudentInFirstYearOfMaster, newStudentInSecondYearOfMaster);

        List<StudentAccount> studentAccountsToBeAddedInDB = this.createStudentAccountsToBeAddedInDB(newStudentsInFirstYearOfLicenta);

        List<Student> listOfStudentsCareNuAplicaLaMaster = this.getListOfStudentsCareNuAplicaLaMaster(previousStudentsInFourthYearOfLicenta,
                totalNumberOfStudentsCareAplicaPentruMaster);

        List<Student> listOfStudentsToBeDeletedFromDB = this.getListOfStudentsThatWillBeDeleted(previousStudentsInFourthYearOfLicenta, listOfStudentsCareNuAplicaLaMaster);

        studentAccountRepository.saveAll(studentAccountsToBeAddedInDB);

        studentRepository.saveAll(studentsToBeAddedInDB);
        studentRepository.deleteAll(listOfStudentsToBeDeletedFromDB);
        return studentRepository.findAllByAnUniversitar(anUniversitar);
    }

    private List<Student> getListOfStudentsThatWillBeDeleted(List<Student> previousStudentsInSecondYearOfMaster, List<Student> listOfStudentsCareNuAplicaLaMaster) {
        return Stream.of(previousStudentsInSecondYearOfMaster, listOfStudentsCareNuAplicaLaMaster)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<Student> getListOfStudentsCareNuAplicaLaMaster(List<Student> previousStudentsInFourthYearOfLicenta,
                                                                int totalNumberOfNewStudentsInFirstYear) {
        List<Student> listOfStudentsCareNuAplicaLaMaster = new ArrayList<>();
        for (int i = totalNumberOfNewStudentsInFirstYear; i < previousStudentsInFourthYearOfLicenta.size(); i++) {
            listOfStudentsCareNuAplicaLaMaster.add(previousStudentsInFourthYearOfLicenta.get(i));
        }
        return listOfStudentsCareNuAplicaLaMaster;
    }

    private List<Student> getListOfStudentsToBeAddedInDB(List<Student> newStudentsInFirstYearOfLicenta,
                                                         List<Student> newStudentsInSecondAndFourthYearOfLicenta,
                                                         List<Student> newStudentsInThirdYearOfLicenta,
                                                         List<Student> newStudentInFirstYearOfMaster,
                                                         List<Student> newStudentInSecondYearOfMaster) {
        return Stream.of(newStudentsInFirstYearOfLicenta, newStudentsInSecondAndFourthYearOfLicenta, newStudentsInThirdYearOfLicenta,
                        newStudentInFirstYearOfMaster, newStudentInSecondYearOfMaster)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<StudentAccount> createStudentAccountsToBeAddedInDB(List<Student> students) {
        return students.stream()
                .map(studentAccountService::createNewStudentAccount)
                .toList();
    }

    private int getTotalNumberOfStudentsInFirstYearOfLicenta(Integer previousNumberOfStudentsInFourthYearOfLicenta,
                                                             Integer totalNumberOfStudentsCareAplicaPentruMaster,
                                                             Integer difference) {
        int totalNumberOfStudentsLeavingAfterFourthYearOfLicenta = previousNumberOfStudentsInFourthYearOfLicenta - totalNumberOfStudentsCareAplicaPentruMaster; // vin aia de anu 1 in locul lor
        return difference + totalNumberOfStudentsLeavingAfterFourthYearOfLicenta;
    }

    private int getRandomNumberOfStudentiCareAplicaPentruMaster(int previousNumberOfStudentsInFourthYearOfLicenta) {
        return new Random().nextInt(0, previousNumberOfStudentsInFourthYearOfLicenta + 1);
    }

    private List<Student> createListOfNewStudentsInFirstYearOfMaster(List<Student> previousStudentsInFourthYearOfLicenta,
                                                                     int totalNumberOfNewStudentsInFirstYearOfMaster,
                                                                     Integer anUniversitar) {
        List<Student> newStudentsInFirstYearOfMaster = new ArrayList<>();
        for (int i = 0; i < totalNumberOfNewStudentsInFirstYearOfMaster; i++) {
            newStudentsInFirstYearOfMaster.add(this.buildNewStudentInFirstYearOfMaster(previousStudentsInFourthYearOfLicenta.get(i), anUniversitar));
        }
        return newStudentsInFirstYearOfMaster;
    }

    private Student buildNewStudentInFirstYearOfMaster(Student studentFromPreviousYear, Integer anUniversitar) {
        return Student.builder()
                .nume(studentFromPreviousYear.getNume())
                .prenume(studentFromPreviousYear.getPrenume())
                .an(ONE.getValue())
                .medie((1D + (10D - 1D) * new Random().nextDouble()))
                .cnp(studentFromPreviousYear.getCnp())
                .zi_de_nastere(studentFromPreviousYear.getZi_de_nastere())
                .judet(studentFromPreviousYear.getJudet())
                .myToken(shuffleString(studentFromPreviousYear.getNume() + studentFromPreviousYear.getPrenume()))
                .friendToken("null")
                .camin_preferat("null")
                .genSexual(studentFromPreviousYear.getGenSexual())
                .flagCazSpecial(studentFromPreviousYear.getFlagCazSpecial())
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.TRUE)
                .master(studentsLoader.getRandomMaster())
                .build();
    }

    private List<Student> createListOfNewStudentsInFirstYearOfLicenta(int totalNumberOfNewStudentsInFirstYear, Integer anUniversitar) {
        List<Student> newStudentsInFirstYearOfLicenta = new ArrayList<>();
        for (int i = 0; i < totalNumberOfNewStudentsInFirstYear; i++) {
            newStudentsInFirstYearOfLicenta.add(this.buildNewStudentInFirstYearOfLicenta(anUniversitar));
        }
        return newStudentsInFirstYearOfLicenta;
    }

    private Student buildNewStudentInSecondYearOfMaster(Student studentFromPreviousYear, Integer anUniversitar) {
        return Student.builder()
                .nume(studentFromPreviousYear.getNume())
                .prenume(studentFromPreviousYear.getPrenume())
                .an(TWO.getValue())
                .medie((1D + (10D - 1D) * new Random().nextDouble()))
                .cnp(studentFromPreviousYear.getCnp())
                .zi_de_nastere(studentFromPreviousYear.getZi_de_nastere())
                .judet(studentFromPreviousYear.getJudet())
                .myToken(shuffleString(studentFromPreviousYear.getNume() + studentFromPreviousYear.getPrenume()))
                .friendToken("null")
                .camin_preferat("null")
                .genSexual(studentFromPreviousYear.getGenSexual())
                .flagCazSpecial(studentFromPreviousYear.getFlagCazSpecial())
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.TRUE)
                .master(studentFromPreviousYear.getMaster())
                .build();
    }

    private Student buildNewStudentInFirstYearOfLicenta(Integer anUniversitar) {
        String randomNume = nameRandomizer.getAlphaNumericString(5);
        String randomPrenume = nameRandomizer.getAlphaNumericString(5);

        String randomDoB = doBandCNPandGenderRandomizer.getDoBLicenta();
        Gender randomGender = doBandCNPandGenderRandomizer.getGender();
        String randomCNP = doBandCNPandGenderRandomizer.getCNP(randomDoB, randomGender);

        return Student.builder()
                .nume(randomNume)
                .prenume(randomPrenume)
                .grupa(this.getNewGrupaForFirstOrThirdYear(ONE))
                .serie(ygsRandomizer.getRandomSeries())
                .an(ONE.getValue())
                .medie((1D + (10D - 1D) * new Random().nextDouble()))
                .myToken(shuffleString(randomNume + randomPrenume))
                .zi_de_nastere(randomDoB)
                .cnp(randomCNP)
                .genSexual(randomGender)
                .judet(countyManager.getCountyFromTwoDigitCode(randomCNP.substring(7, 9)))
                .friendToken("null")
                .camin_preferat("null")
                .flagCazSpecial("Nu")
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.FALSE)
                .build();
    }

    private Student buildNewStudentInThirdYear(Student studentFromPreviousYear, Integer anUniversitar) {
        return Student.builder()
                .nume(studentFromPreviousYear.getNume())
                .prenume(studentFromPreviousYear.getPrenume())
                .grupa(this.getNewGrupaForFirstOrThirdYear(THREE))
                .serie(studentFromPreviousYear.getSerie())
                .an(THREE.getValue())
                .medie((1D + (10D - 1D) * new Random().nextDouble()))
                .cnp(studentFromPreviousYear.getCnp())
                .zi_de_nastere(studentFromPreviousYear.getZi_de_nastere())
                .judet(studentFromPreviousYear.getJudet())
                .myToken(shuffleString(studentFromPreviousYear.getNume() + studentFromPreviousYear.getPrenume()))
                .friendToken("null")
                .camin_preferat("null")
                .genSexual(studentFromPreviousYear.getGenSexual())
                .flagCazSpecial(studentFromPreviousYear.getFlagCazSpecial())
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.FALSE)
                .build();
    }

    private Student buildNewStudentInSecondOrFourthYear(Student studentFromPreviousYear, Integer anUniversitar) {
        return Student.builder()
                .nume(studentFromPreviousYear.getNume())
                .prenume(studentFromPreviousYear.getPrenume())
                .grupa(this.incrementAndGetNewGrupaForSecondAndFourthYear(studentFromPreviousYear.getGrupa()))
                .serie(ygsRandomizer.getRandomSeries())
                .an(ONE.getValue().equals(studentFromPreviousYear.getAn()) ? TWO.getValue() : FOUR.getValue())
                .medie((1D + (10D - 1D) * new Random().nextDouble()))
                .cnp(studentFromPreviousYear.getCnp())
                .zi_de_nastere(studentFromPreviousYear.getZi_de_nastere())
                .judet(studentFromPreviousYear.getJudet())
                .myToken(shuffleString(studentFromPreviousYear.getNume() + studentFromPreviousYear.getPrenume()))
                .friendToken("null")
                .camin_preferat("null")
                .genSexual(studentFromPreviousYear.getGenSexual())
                .flagCazSpecial(studentFromPreviousYear.getFlagCazSpecial())
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.FALSE)
                .build();
    }

    private String getNewGrupaForFirstOrThirdYear(AnDeStudiu anDeStudiu) {
        String newRandomGroup = ygsRandomizer.getRandomGroup();
        return newRandomGroup.substring(0, 1) + anDeStudiu.getValue() + newRandomGroup.substring(2);
    }

    private String incrementAndGetNewGrupaForSecondAndFourthYear(String oldGrupa) {
        int newYear = Integer.parseInt(String.valueOf(oldGrupa.charAt(1))) + 1;
        return oldGrupa.substring(0, 1) + newYear + oldGrupa.substring(2);
    }

    private List<Student> getStudentsByYearFromPreviousAnUniversitar(List<Student> studentsOfPreviousAnUniversitar, AnDeStudiu anDeStudiu, boolean isMasterand) {
        if (Boolean.TRUE.equals(isMasterand)) {
            return studentsOfPreviousAnUniversitar.stream()
                    .filter(student -> anDeStudiu.getValue().compareTo(student.getAn()) == 0 && student.getIsMasterand().equals(true))
                    .toList();
        }
        return studentsOfPreviousAnUniversitar.stream()
                .filter(student -> anDeStudiu.getValue().compareTo(student.getAn()) == 0 && student.getIsMasterand().equals(false))
                .toList();
    }

    private List<Student> getStudentsByYearFromPreviousAnUniversitar(List<Student> studentsOfPreviousAnUniversitar) {
        return studentsOfPreviousAnUniversitar.stream()
                .filter(student -> (ONE.getValue().compareTo(student.getAn()) == 0
                        || THREE.getValue().compareTo(student.getAn()) == 0) && student.getIsMasterand().equals(false))
                .toList();
    }

    private List<Camin> getCamineOfAnUniversitar(Integer anUniversitar) {
        Integer numberOfCamineForAnUniversitar = caminRepository.countAllByAnUniversitar(anUniversitar);
        if (ZERO.compareTo(numberOfCamineForAnUniversitar) == 0) {
            return this.createCamineNoiOfAnUniversitar(anUniversitar);
        } else {
            return caminRepository.findAllByAnUniversitar(anUniversitar);
        }
    }

    private List<Camin> createCamineNoiOfAnUniversitar(Integer anUniversitar) {
        List<Camin> camine = new ArrayList<>();

        camine.add(Camin.builder()
                .numeCamin("Leu A")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .anUniversitar(anUniversitar)
                .build());

        camine.add(Camin.builder()
                .numeCamin("Leu C")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .anUniversitar(anUniversitar)
                .build());

        camine.add(Camin.builder()
                .numeCamin("P20")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .anUniversitar(anUniversitar)
                .build());

        camine.add(Camin.builder()
                .numeCamin("P23")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .anUniversitar(anUniversitar)
                .build());

        return caminRepository.saveAll(camine);
    }

}
