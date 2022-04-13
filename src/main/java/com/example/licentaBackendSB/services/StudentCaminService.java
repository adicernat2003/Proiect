package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.model.dtos.StudentAplicantDto;
import com.example.licentaBackendSB.model.entities.StudentAplicant;
import com.example.licentaBackendSB.repositories.StudentCaminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class StudentCaminService {

    private final StudentCaminRepository studentCaminRepository;
    private final StudentConverter studentConverter;

    public List<StudentAplicantDto> getStudents(String numeCamin, Integer anUniversitar) {
        return studentCaminRepository.findAllByNumeCaminAndAnUniversitar(numeCamin, anUniversitar)
                .stream()
                .map(studentConverter::mapStudentCaminEntityToDto)
                .toList();
    }

    public void introduceNewStudentCamin(StudentAplicant newStudentAplicant) {
        studentCaminRepository.save(newStudentAplicant);
    }

    /* ~~~~~~~~~~~ Delete Student in the Camin Table Corespunzator ~~~~~~~~~~~ */
    public void deleteStudentInCamin(StudentAplicant selectedStudentAplicant, String numeCamin) {
        studentCaminRepository.removeStudentCaminByMyTokenAndCnpAndNumeAndPrenumeAndNumeCamin(
                selectedStudentAplicant.getMyToken(),
                selectedStudentAplicant.getCnp(),
                selectedStudentAplicant.getNume(),
                selectedStudentAplicant.getPrenume(),
                numeCamin);
    }

    /*  ~~~~~~~~~~~ Update Student from Camin Leu A with FriendToken ~~~~~~~~~~~ */
    public void updateFriendTokenOfStudentInCamin(StudentAplicant studentAplicant, String numeCamin) {
        Optional<StudentAplicant> studentDinCaminA = studentCaminRepository.getStudentCaminByCnpAndNumeCamin(studentAplicant.getCnp(), numeCamin);
        studentDinCaminA.get().setFriendToken(studentAplicant.getFriendToken());
        studentDinCaminA.ifPresent(student -> studentCaminRepository.updateFriendTokenFromStudentInCamin(student.getFriendToken(), student.getCnp(), numeCamin));
    }

}
