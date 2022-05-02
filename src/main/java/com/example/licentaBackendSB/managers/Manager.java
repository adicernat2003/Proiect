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

import static com.example.licentaBackendSB.constants.Constants.*;

@Component
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class Manager {

    private final StudentRepository studentRepository;
    private final StudentConverter studentConverter;
    private final StringUtils stringUtils;
    private final CaminRepository caminRepository;
    private final CameraRepository cameraRepository;

    public String getRandomRoomNumber(String numeCamin) {
        int roomNum = new Random().nextInt(9999 - 1000) + 1000;
        return numeCamin.toUpperCase() + "-" + roomNum;
    }

    public List<Integer> getListOfYears(Integer anUniversitar) {
        return IntStream.rangeClosed(2021, 2099)
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
        return cameraRepository.findAllByAnUniversitar(student.getAnUniversitar()).stream()
                .filter(camera -> !student.getCamerePreferate().contains(camera))
                .map(camera -> camera.getNumarCamera() + ", " + stringUtils.mapIntegerNumarPersoaneCameraToString(camera.getNumarTotalPersoane()))
                .toList();
    }

    public List<String> getListaOptiuniCamereForStudent(Student student) {
        return this.getListOptiuniCamere().stream()
                .map(stringUtils::mapStringNumarPersoaneCameraToInteger)
                .filter(integer -> !student.getNumarLocuriCamera().contains(integer))
                .map(stringUtils::mapIntegerNumarPersoaneCameraToString)
                .toList();
    }

    public List<String> getListOfCaminePreferate(Student student) {
        return caminRepository.findAllByAnUniversitar(student.getAnUniversitar()).stream()
                .filter(camin -> !student.getCaminePreferate().contains(camin))
                .map(Camin::getNumeCamin)
                .toList();
    }

    public List<StudentDto> getListOfStudentsForStudent(Student student) {
        return this.getListOfAllStudentsBasedOnGender(student.getGenSexual(), student.getAnUniversitar()).stream()
                .filter(friend -> !student.getFriends().contains(friend) && !friend.getId().equals(student.getId()))
                .map(studentConverter::mapStudentEntityToDto)
                .toList();
    }

    private List<Student> getListOfAllStudentsBasedOnGender(Gender gender, Integer anUniversitar) {
        return studentRepository.findAllByGenSexualAndAnUniversitar(gender, anUniversitar);
    }

    public List<String> getListOptiuniCamere() {
        return List.of(O_PERSOANA, DOUA_PERSOANE, TREI_PERSOANE, PATRU_PERSOANE);
    }

    public int getMaxNumberOfStudentiInCamera(Student student) {
        return student.getNumarLocuriCamera() != null && student.getNumarLocuriCamera().size() > 0 ?
                student.getNumarLocuriCamera().stream()
                        .max(Integer::compareTo)
                        .get() : 0;
    }

}
