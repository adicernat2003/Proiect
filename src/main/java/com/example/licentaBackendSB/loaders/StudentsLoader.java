package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.entities.Student;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class StudentsLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) {
        log.info("Loading data from StudentLoader...");

        List<Student> studentsDB = Student.hardcodedStudentsList;

        studentRepository.saveAll(studentsDB);
    }
}
