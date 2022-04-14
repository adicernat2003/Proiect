package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
import com.example.licentaBackendSB.services.StudentAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class StudentAccountsLoader implements CommandLineRunner {

    private final StudentAccountRepository studentAccountRepository;
    private final StudentAccountService studentAccountService;
    private final DoBandCNPandGenderRandomizer doBandCNPandGenderRandomizer;

    public static List<StudentAccount> studentAccountsDB;

    public List<StudentAccount> hardcodeStudentsAccountsDB(List<Student> students) {
        List<StudentAccount> studentAccounts = new ArrayList<>();
        students.forEach((student) -> studentAccounts.add(studentAccountService.createNewStudentAccount(student)));
        return studentAccounts;
    }

    @Override
    public void run(String... args) {
        log.info("Loading data from StudentAccountsLoader...");

        studentAccountRepository.saveAll(studentAccountsDB);
    }

}
