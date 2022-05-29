package com.example.licentaBackendSB.managers;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.repositories.CameraRepository;
import com.example.licentaBackendSB.repositories.CaminRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import com.example.licentaBackendSB.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static com.example.licentaBackendSB.constants.Constants.DEFAULT_YEAR;

@Component
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class Manager {

    public static final Random random = new Random();

    private final StudentRepository studentRepository;
    private final StudentConverter studentConverter;
    private final StringUtils stringUtils;
    private final CaminRepository caminRepository;
    private final CameraRepository cameraRepository;

    public Integer getRandomPrioritate() {
        return random.nextInt(0, 5);
    }

    public String getRandomRoomNumber(String numeCamin) {
        int roomNum = random.nextInt(9999 - 1000) + 1000;
        return numeCamin.toUpperCase() + "-" + roomNum;
    }

    public Double getRandomMedie() {
        return 1D + (10D - 1D) * random.nextDouble();
    }

    public List<Integer> getListOfYears(Integer anUniversitar) {
        return IntStream.rangeClosed(DEFAULT_YEAR, 2099)
                .boxed()
                .filter(an -> !an.equals(anUniversitar))
                .toList();
    }

    public List<Integer> getListOfYearsForStudentPage(Student student, Integer anUniversitar) {
        Optional<Integer> primulAnUniversitarAlStudentuluiOpt = studentRepository.getLowestAnUniversitarForStudent(student.getNume(), student.getPrenume());
        if (primulAnUniversitarAlStudentuluiOpt.isPresent()) {
            int primulAnUniversitarAlStudentului = primulAnUniversitarAlStudentuluiOpt.get();
            int anStudent = student.getAn();
            if (Boolean.FALSE.equals(student.getIsMasterand())) {
                return this.getListOfYearsForStudentLicenta(primulAnUniversitarAlStudentului, anUniversitar, anStudent);
            }
            return this.getListOfYearsForStudentMaster(primulAnUniversitarAlStudentului, anUniversitar, anStudent);
        } else {
            throw new IllegalArgumentException("Studentul nu exista!");
        }
    }

    private List<Integer> getListOfYearsForStudentLicenta(Integer primulAnUniversitarAlStudentului, Integer anUniversitar, Integer anStudent) {
        return IntStream.rangeClosed(primulAnUniversitarAlStudentului, primulAnUniversitarAlStudentului + 4 - anStudent + anUniversitar - primulAnUniversitarAlStudentului)
                .boxed()
                .filter(an -> !an.equals(anUniversitar))
                .toList();
    }

    private List<Integer> getListOfYearsForStudentMaster(Integer primulAnUniversitarAlStudentului, Integer anUniversitar, Integer anStudent) {
        return IntStream.rangeClosed(primulAnUniversitarAlStudentului, primulAnUniversitarAlStudentului + 2 - anStudent + anUniversitar - primulAnUniversitarAlStudentului)
                .boxed()
                .filter(an -> !an.equals(anUniversitar))
                .toList();
    }

    public List<String> getListOfCamerePreferateForStudent(Student student) {
        return cameraRepository.findAllByAnUniversitar(student.getAnUniversitar())
                .stream()
                .filter(camera -> !cameraRepository.getAllCamerePreferateOfStudent(student.getId()).contains(camera)
                        && !caminRepository.getAllUndesiredCamineOfStudent(student.getId()).contains(camera.getCamin()))
                .map(camera -> camera.getNumarCamera() + ", " + stringUtils.mapIntegerNumarPersoaneCameraToString(camera.getNumarTotalPersoane()))
                .toList();
    }

    public List<String> getListOfCamineNedoriteForStudent(Student student) {
        return caminRepository.findAllByAnUniversitar(student.getAnUniversitar())
                .stream()
                .filter(camin -> !caminRepository.getAllUndesiredCamineOfStudent(student.getId()).contains(camin)
                        && !caminRepository.getAllCamineOfStudentPreferences(student.getId()).contains(camin))
                .map(Camin::getNumeCamin)
                .toList();
    }

    public List<StudentDto> getListOfStudentsForStudent(Student student) {
        return this.getListOfAllStudentsBasedOnGender(student.getGenSexual(), student.getAnUniversitar())
                .stream()
                .filter(friend -> !student.getFriends().contains(friend) && !friend.getId().equals(student.getId()))
                .map(studentConverter::mapStudentEntityToDto)
                .toList();
    }

    private List<Student> getListOfAllStudentsBasedOnGender(Gender gender, Integer anUniversitar) {
        return studentRepository.findAllByGenSexualAndAnUniversitar(gender, anUniversitar);
    }

}
