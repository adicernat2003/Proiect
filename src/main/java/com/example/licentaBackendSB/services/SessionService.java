package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.enums.AnDeStudiu;
import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.enums.Session;
import com.example.licentaBackendSB.loaders.StudentsLoader;
import com.example.licentaBackendSB.managers.Manager;
import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.randomizers.CountyManager;
import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
import com.example.licentaBackendSB.others.randomizers.nameRandomizer;
import com.example.licentaBackendSB.others.randomizers.ygsRandomizer;
import com.example.licentaBackendSB.repositories.CameraRepository;
import com.example.licentaBackendSB.repositories.CaminRepository;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.example.licentaBackendSB.constants.Constants.*;
import static com.example.licentaBackendSB.enums.AnDeStudiu.*;
import static com.example.licentaBackendSB.enums.Session.CAMIN;
import static com.example.licentaBackendSB.managers.Manager.random;
import static org.hibernate.type.IntegerType.ZERO;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final CaminRepository caminRepository;
    private final StudentRepository studentRepository;
    private final StudentAccountRepository studentAccountRepository;
    private final DoBandCNPandGenderRandomizer doBandCNPandGenderRandomizer;
    private final CountyManager countyManager;
    private final StudentsLoader studentsLoader;
    private final StudentAccountService studentAccountService;
    private final CaminService caminService;
    private final StudentService studentService;
    private final Manager manager;
    private final CameraRepository cameraRepository;

    public List<?> getNewSession(Integer anUniversitar, Session session) {
        Integer numberOfStudentsForAnUniversitar = studentRepository.countAllByAnUniversitar(anUniversitar);
        if (ZERO.compareTo(numberOfStudentsForAnUniversitar) == 0) {
            int lastYearOfStudents;
            for (lastYearOfStudents = anUniversitar - 1; lastYearOfStudents >= DEFAULT_YEAR; lastYearOfStudents--) {
                Integer numberOfStudentsOfCurrentYear = studentRepository.countAllByAnUniversitar(lastYearOfStudents);
                if (ZERO.compareTo(numberOfStudentsOfCurrentYear) != 0) {
                    break;
                }
            }
            for (int year = lastYearOfStudents + 1; year <= anUniversitar; year++) {
                this.createCamineNoiOfAnUniversitar(year);
                this.createStudentsOfAnUniversitar(year);
                this.createCamereNoiOfAnUniversitar(year);
            }
        }
        if (CAMIN.equals(session)) {
            return caminService.getCamineByAnUniversitar(anUniversitar);
        }
        return studentService.getStudentsByAnUniversitar(anUniversitar);
    }

    private void createStudentsOfAnUniversitar(Integer anUniversitar) {
        List<Student> previousStudentOfAnUniversitar = studentRepository.findAllByAnUniversitar(anUniversitar - 1);

        List<Student> previousStudentsInFirstAndThirdYearOfLicenta = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar);
        List<Student> previousStudentsInSecondYearOfLicenta = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar, TWO, false);
        List<Student> previousStudentsInFourthYearOfLicenta = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar, FOUR, false);

        List<Student> previousStudentsInFirstYearOfMaster = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar, ONE, true);
        List<Student> previousStudentsInSecondYearOfMaster = this.getStudentsByYearFromPreviousAnUniversitar(previousStudentOfAnUniversitar, TWO, true);

        int totalNumberOfStudentsCareAplicaPentruMaster = this.getRandomNumberOfStudentiCareAplicaPentruMaster(previousStudentsInFourthYearOfLicenta.size());
        int totalNumberOfNewStudentsInFirstYearOfLicenta = this.getTotalNumberOfStudentsInFirstYearOfLicenta(previousStudentsInFourthYearOfLicenta.size(),
                totalNumberOfStudentsCareAplicaPentruMaster, previousStudentsInSecondYearOfMaster.size());

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

        studentAccountRepository.saveAll(studentAccountsToBeAddedInDB);
        studentRepository.saveAll(studentsToBeAddedInDB);
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
                                                             Integer previousNumberOfStudentsInSecondYearOfMaster) {
        int totalNumberOfStudentsLeavingAfterFourthYearOfLicenta = previousNumberOfStudentsInFourthYearOfLicenta
                - totalNumberOfStudentsCareAplicaPentruMaster;
        return previousNumberOfStudentsInSecondYearOfMaster + totalNumberOfStudentsLeavingAfterFourthYearOfLicenta;
    }

    private int getRandomNumberOfStudentiCareAplicaPentruMaster(int previousNumberOfStudentsInFourthYearOfLicenta) {
        return random.nextInt(0, previousNumberOfStudentsInFourthYearOfLicenta + 1);
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
        return new Student().toBuilder()
                .nume(studentFromPreviousYear.getNume())
                .prenume(studentFromPreviousYear.getPrenume())
                .an(ONE.getValue())
                .medie(manager.getRandomMedie())
                .cnp(studentFromPreviousYear.getCnp())
                .zi_de_nastere(studentFromPreviousYear.getZi_de_nastere())
                .judet(studentFromPreviousYear.getJudet())
                .genSexual(studentFromPreviousYear.getGenSexual())
                .isCazSpecial(studentFromPreviousYear.getIsCazSpecial())
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.TRUE)
                .master(studentsLoader.getRandomMaster())
                .prioritate(manager.getRandomPrioritate())
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
        return new Student().toBuilder()
                .nume(studentFromPreviousYear.getNume())
                .prenume(studentFromPreviousYear.getPrenume())
                .an(TWO.getValue())
                .medie(manager.getRandomMedie())
                .cnp(studentFromPreviousYear.getCnp())
                .zi_de_nastere(studentFromPreviousYear.getZi_de_nastere())
                .judet(studentFromPreviousYear.getJudet())
                .genSexual(studentFromPreviousYear.getGenSexual())
                .isCazSpecial(studentFromPreviousYear.getIsCazSpecial())
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.TRUE)
                .master(studentFromPreviousYear.getMaster())
                .prioritate(studentFromPreviousYear.getPrioritate())
                .build();
    }

    private Student buildNewStudentInFirstYearOfLicenta(Integer anUniversitar) {
        String randomNume = nameRandomizer.getAlphaNumericString(5);
        String randomPrenume = nameRandomizer.getAlphaNumericString(5);

        String randomDoB = doBandCNPandGenderRandomizer.getDoBLicenta(anUniversitar);
        Gender randomGender = doBandCNPandGenderRandomizer.getGender();
        String randomCNP = doBandCNPandGenderRandomizer.getCNP(randomDoB, randomGender);

        return new Student().toBuilder()
                .nume(randomNume)
                .prenume(randomPrenume)
                .grupa(this.getNewGrupaForFirstOrThirdYear(ONE))
                .serie(ygsRandomizer.getRandomSeries())
                .an(ONE.getValue())
                .medie(manager.getRandomMedie())
                .zi_de_nastere(randomDoB)
                .cnp(randomCNP)
                .genSexual(randomGender)
                .isCazSpecial(Boolean.FALSE)
                .judet(countyManager.getCountyFromTwoDigitCode(randomCNP.substring(7, 9)))
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.FALSE)
                .prioritate(manager.getRandomPrioritate())
                .build();
    }

    private Student buildNewStudentInThirdYear(Student studentFromPreviousYear, Integer anUniversitar) {
        return new Student().toBuilder()
                .nume(studentFromPreviousYear.getNume())
                .prenume(studentFromPreviousYear.getPrenume())
                .grupa(this.getNewGrupaForFirstOrThirdYear(THREE))
                .serie(ygsRandomizer.getRandomSeries())
                .an(THREE.getValue())
                .medie(manager.getRandomMedie())
                .cnp(studentFromPreviousYear.getCnp())
                .zi_de_nastere(studentFromPreviousYear.getZi_de_nastere())
                .judet(studentFromPreviousYear.getJudet())
                .genSexual(studentFromPreviousYear.getGenSexual())
                .isCazSpecial(studentFromPreviousYear.getIsCazSpecial())
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.FALSE)
                .prioritate(studentFromPreviousYear.getPrioritate())
                .build();
    }

    private Student buildNewStudentInSecondOrFourthYear(Student studentFromPreviousYear, Integer anUniversitar) {
        return new Student().toBuilder()
                .nume(studentFromPreviousYear.getNume())
                .prenume(studentFromPreviousYear.getPrenume())
                .grupa(this.incrementAndGetNewGrupaForSecondAndFourthYear(studentFromPreviousYear.getGrupa()))
                .serie(studentFromPreviousYear.getSerie())
                .an(ONE.getValue().equals(studentFromPreviousYear.getAn()) ? TWO.getValue() : FOUR.getValue())
                .medie(manager.getRandomMedie())
                .cnp(studentFromPreviousYear.getCnp())
                .zi_de_nastere(studentFromPreviousYear.getZi_de_nastere())
                .judet(studentFromPreviousYear.getJudet())
                .genSexual(studentFromPreviousYear.getGenSexual())
                .isCazSpecial(studentFromPreviousYear.getIsCazSpecial())
                .anUniversitar(anUniversitar)
                .isMasterand(Boolean.FALSE)
                .prioritate(studentFromPreviousYear.getPrioritate())
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

    private void createCamineNoiOfAnUniversitar(Integer anUniversitar) {
        caminRepository.save(new Camin().toBuilder()
                .numeCamin(LEU_A)
                .anUniversitar(anUniversitar)
                .build());

        caminRepository.save(new Camin().toBuilder()
                .numeCamin(LEU_C)
                .anUniversitar(anUniversitar)
                .build());

        caminRepository.save(new Camin().toBuilder()
                .numeCamin(P20)
                .anUniversitar(anUniversitar)
                .build());

        caminRepository.save(new Camin().toBuilder()
                .numeCamin(P23)
                .anUniversitar(anUniversitar)
                .build());
    }

    private void createCamereNoiOfAnUniversitar(Integer anUniversitar) {
        List<Camin> camine = caminRepository.findAllByAnUniversitar(anUniversitar);
        for (Camin camin : camine) {
            List<Object[]> optiuniCamere = caminRepository.getAllCamereOptiuniByNumeCaminAndAnUniversitar(camin.getNumeCamin(), DEFAULT_YEAR);
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < (Integer) optiuniCamere.get(0)[j]; i++) {
                    cameraRepository.save(new Camera().toBuilder()
                            .anUniversitar(camin.getAnUniversitar())
                            .numarTotalPersoane(j + 1)
                            .camin(camin)
                            .numarCamera(manager.getRandomRoomNumber(camin.getNumeCamin()))
                            .build());
                }
            }
        }
    }

}
