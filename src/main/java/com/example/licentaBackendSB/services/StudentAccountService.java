package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.licentaBackendSB.constants.Constants.STUDENT;

@Service
@RequiredArgsConstructor
public class StudentAccountService {

    private final StudentAccountRepository studentAccountRepository;
    private final DoBandCNPandGenderRandomizer doBandCNPandGenderRandomizer;

    public StudentAccount getLoggedStudentAccount() {
        LoggedAccount loggedAccount = new LoggedAccount();

        List<StudentAccount> studentAccountsDB = studentAccountRepository.findAll();
        return studentAccountsDB.stream()
                .filter(studentAccount -> studentAccount.getCnp().equals(loggedAccount.getLoggedUsername()))
                .findFirst()
                .orElse(null);
    }

    public void updateStudent(String studentCnp, StudentDto newStudent) {
        Optional<StudentAccount> studentAccountOptional = studentAccountRepository.findByCnp(studentCnp);
        if (studentAccountOptional.isPresent()) {
            StudentAccount foundStudent = studentAccountOptional.get();

            if (newStudent.getNume() != null
                    && newStudent.getNume().length() > 0
                    && !foundStudent.getNume().equals(newStudent.getNume())) {
                foundStudent.setNume(newStudent.getNume());
            }

            if (newStudent.getPrenume() != null
                    && newStudent.getPrenume().length() > 0
                    && !foundStudent.getPrenume().equals(newStudent.getPrenume())) {
                foundStudent.setPrenume(newStudent.getPrenume());
            }

            if (newStudent.getCnp() != null
                    && newStudent.getCnp().length() > 0
                    && !foundStudent.getCnp().equals(newStudent.getCnp())) {
                foundStudent.setCnp(newStudent.getCnp());
                foundStudent.setUsername(newStudent.getCnp());
            }

            if (newStudent.getZi_de_nastere() != null
                    && newStudent.getZi_de_nastere().length() > 0
                    && !foundStudent.getZi_de_nastere().equals(newStudent.getZi_de_nastere())) {
                foundStudent.setZi_de_nastere(newStudent.getZi_de_nastere());
                foundStudent.setPassword(doBandCNPandGenderRandomizer.splitDoBbyDot(newStudent.getZi_de_nastere()));
            }

            studentAccountRepository.save(foundStudent);
        } else {
            throw new IllegalStateException("Student account with cnp " + studentCnp + " does not exist");
        }
    }

    public StudentAccount createNewStudentAccount(Student student) {
        return StudentAccount.builder()
                .nume(student.getNume())
                .prenume(student.getPrenume())
                .zi_de_nastere(student.getZi_de_nastere())
                .username(student.getCnp())
                .password(doBandCNPandGenderRandomizer.splitDoBbyDot(student.getZi_de_nastere()))
                .cnp(student.getCnp())
                .autoritate(STUDENT)
                .isActive(Boolean.TRUE)
                .build();
    }

}