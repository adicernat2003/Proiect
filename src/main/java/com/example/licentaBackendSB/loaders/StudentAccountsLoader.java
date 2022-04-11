package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.entities.Student;
import com.example.licentaBackendSB.entities.StudentAccount;
import com.example.licentaBackendSB.repositories.StudentAccountsDBRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class StudentAccountsLoader implements CommandLineRunner {

    private final StudentAccountsDBRepository studentAccountsDBRepository;

    @Override
    public void run(String... args) {
        log.info("Loading data from StudentAccountsLoader...");

        List<Student> studentsDB = Student.hardcodedStudentsList;
        List<StudentAccount> studentAccountsDB = StudentAccount.hardcodeStudentsAccountsDB(studentsDB);

        studentAccountsDBRepository.saveAll(studentAccountsDB);
    }
}
