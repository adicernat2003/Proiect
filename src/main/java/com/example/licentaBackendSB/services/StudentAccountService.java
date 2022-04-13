package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class StudentAccountService {

    //Field
    private final StudentAccountRepository studentAccountRepository;

    /*  ~~~~~~~~~~~ Get Logged Account from Current Session ~~~~~~~~~~~ */
    public StudentAccount getLoggedStudentAccount() {
        StudentAccount result = new StudentAccount();
        LoggedAccount loggedAccount = new LoggedAccount();

        List<StudentAccount> studentAccountsDB = studentAccountRepository.findAll();
        for (StudentAccount it : studentAccountsDB) {
            if (it.getCnp().equals(loggedAccount.getLoggedUsername())) {
                result = it;
                break;
            }
        }

        return result;
    }

    /*  ~~~~~~~~~~~ Update Student ~~~~~~~~~~~ */
    public void updateStudent(String studentCnp, StudentDto newStudent) {
        Optional<StudentAccount> studentAccountOptional = studentAccountRepository.findByCnp(studentCnp);
        if (studentAccountOptional.isPresent()) {
            StudentAccount foundStudent = studentAccountOptional.get();
            /** update nume*/
            if (newStudent.getNume() != null
                    && newStudent.getNume().length() > 0
                    && !foundStudent.getNume().equals(newStudent.getNume())) {
                foundStudent.setNume(newStudent.getNume());
            }

            /** update prenume*/
            if (newStudent.getPrenume() != null
                    && newStudent.getPrenume().length() > 0
                    && !foundStudent.getPrenume().equals(newStudent.getPrenume())) {
                foundStudent.setPrenume(newStudent.getPrenume());
            }

            /** update cnp*/
            if (newStudent.getCnp() != null
                    && newStudent.getCnp().length() > 0
                    && !foundStudent.getCnp().equals(newStudent.getCnp())) {
                foundStudent.setCnp(newStudent.getCnp());
                foundStudent.setUsername(newStudent.getCnp());
            }

            /** update zi_de_nastere*/
            if (newStudent.getZi_de_nastere() != null
                    && newStudent.getZi_de_nastere().length() > 0
                    && !foundStudent.getZi_de_nastere().equals(newStudent.getZi_de_nastere())) {
                foundStudent.setZi_de_nastere(newStudent.getZi_de_nastere());
                foundStudent.setPassword(DoBandCNPandGenderRandomizer.splitDoBbyDot(newStudent.getZi_de_nastere()));
            }

            studentAccountRepository.save(foundStudent);
        } else {
            throw new IllegalStateException("Student account with cnp " + studentCnp + " does not exist");
        }
    }

}