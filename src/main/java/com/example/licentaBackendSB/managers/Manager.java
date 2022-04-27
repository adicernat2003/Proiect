package com.example.licentaBackendSB.managers;

import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class Manager {

    private final StudentRepository studentRepository;

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
}
