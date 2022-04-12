package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.entities.StudentCamin;
import com.example.licentaBackendSB.repositories.StudentCaminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentCaminService {

    private final StudentCaminRepository studentCaminRepository;

    public List<StudentCamin> getStudents(String numeCamin) {
        return studentCaminRepository.getAllByNumeCamin(numeCamin);
    }

    public void introduceNewStudentCamin(StudentCamin newStudentCamin) {
        studentCaminRepository.save(newStudentCamin);
    }

    /* ~~~~~~~~~~~ Delete Student in the Camin Table Corespunzator ~~~~~~~~~~~ */
    public void deleteStudentInCamin(StudentCamin selectedStudentCamin, String numeCamin) {
        studentCaminRepository.deleteByNumePrenumeMyTokenCNP(
                selectedStudentCamin.getMyToken(),
                selectedStudentCamin.getCnp(),
                selectedStudentCamin.getNume(),
                selectedStudentCamin.getPrenume(),
                numeCamin);
    }

    /*  ~~~~~~~~~~~ Update Student from Camin Leu A with FriendToken ~~~~~~~~~~~ */
    @Transactional
    public void updateFriendTokenOfStudentInCamin(StudentCamin studentCamin, String numeCamin) {
        Optional<StudentCamin> studentDinCaminA = studentCaminRepository.getStudentFromCamin(studentCamin.getCnp(), numeCamin);
        studentDinCaminA.get().setFriendToken(studentCamin.getFriendToken());
        studentDinCaminA.ifPresent(student -> studentCaminRepository.updateFriendTokenFromStudentInCamin(student.getFriendToken(), student.getCnp(), numeCamin));
    }

}
