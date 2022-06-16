package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.services.StudentAccountService;
import com.example.licentaBackendSB.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentAccountService studentAccountService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getStudentView(Model model) {
        LoggedAccount loggedAccount = new LoggedAccount();
        model.addAttribute("loggedUsername", loggedAccount.getLoggedUsername());
        model.addAttribute("isDevAcc", loggedAccount.checkIfStandardAccLogged().toString());

        StudentAccount loggedStudentAccount = studentAccountService.getLoggedStudentAccount();
        Integer anUniversitar = studentService.getLowestAnUniversitarForStudent(loggedStudentAccount.getNume(),
                loggedStudentAccount.getPrenume());

        model.addAttribute("anUniversitar", String.valueOf(anUniversitar));
        return "pages/layer 3/student";
    }

    @GetMapping("/students/{anUniversitar}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getStudents(@PathVariable Integer anUniversitar, Model model) {
        model.addAttribute("listOfStudents", studentService.getStudentsByAnUniversitar(anUniversitar));
        model.addAttribute("isAdmin", "student");

        return "pages/layer 4/students table/students_list";
    }

    @GetMapping("/devStudentPage")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getDevStudentPage() {
        return "pages/layer 4/info pages/developer/devStudentPage";
    }
}
