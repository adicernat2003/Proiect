package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
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

    public static List<StudentAccount> studentAccountsDB;

    public static List<StudentAccount> hardcodeStudentsAccountsDB(List<Student> students) {
        List<StudentAccount> studentAccounts = new ArrayList<>();

        for (long i = Student.startIndexing - 1L; i < Student.endIndexing; i++) {
            String username = students.get((int) i).getCnp();
            String password = DoBandCNPandGenderRandomizer.splitDoBbyDot(students.get((int) i).getZi_de_nastere());

            studentAccounts.add(StudentAccount.builder()
                    .nume(students.get((int) i).getNume())
                    .prenume(students.get((int) i).getPrenume())
                    .zi_de_nastere(students.get((int) i).getZi_de_nastere())
                    .username(username)
                    .password(password)
                    .cnp(students.get((int) i).getCnp())
                    .autoritate("STUDENT")
                    .isActive(Boolean.TRUE)
                    .build());
        }
        return studentAccounts;
    }

    @Override
    public void run(String... args) {
        log.info("Loading data from StudentAccountsLoader...");

        studentAccountRepository.saveAll(studentAccountsDB);
    }

}
