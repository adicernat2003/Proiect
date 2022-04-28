package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.model.dtos.StudentAplicantDto;
import com.example.licentaBackendSB.model.entities.Camin;
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
    public void deleteStudentInCamin(StudentAplicant selectedStudentAplicant, String anUniversitar) {
        studentCaminRepository.removeStudentCaminByCnpAndAnUniversitar(selectedStudentAplicant.getCnp(), Integer.parseInt(anUniversitar));
    }

    /*  ~~~~~~~~~~~ Update Student from Camin Leu A with FriendToken ~~~~~~~~~~~ */
    public void updateFriendTokenOfStudentInCamin(StudentAplicant studentAplicant, Camin camin, Integer anUniversitar) {
        Optional<StudentAplicant> studentDinCaminOptional = studentCaminRepository.getStudentCaminByCnpAndAnUniversitarAndNumeCamin(studentAplicant.getCnp(), anUniversitar, camin.getNumeCamin());
        StudentAplicant studentDinCamin = studentDinCaminOptional.get();
        studentDinCamin.setFriendToken(studentAplicant.getFriendToken());
        studentCaminRepository.save(studentDinCamin);
    }

}
