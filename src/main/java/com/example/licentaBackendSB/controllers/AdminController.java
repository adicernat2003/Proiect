package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.entities.Student;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.services.StudentAccountService;
import com.example.licentaBackendSB.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "admin")
@RequiredArgsConstructor
public class AdminController {

    //Field
    private final StudentService studentService;
    private final StudentAccountService studentAccountService;

    /* ~~~~~~~~~~~ AdminView ~~~~~~~~~~~ */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getAdminView(Model model) {
        LoggedAccount loggedAccount = new LoggedAccount();
        model.addAttribute("loggedUsername", loggedAccount.getLoggedUsername());
        model.addAttribute("isDevAcc", loggedAccount.checkIfStandardAccLogged().toString());

        return "pages/layer 3/admin";
    }

    /* ~~~~~~~~~~~ Student List ~~~~~~~~~~~ */
    @GetMapping("students")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getStudents(Model model) {
        model.addAttribute("listOfStudents", studentService.getStudents());
        model.addAttribute("isAdmin", "admin");

        return "pages/layer 4/students table/students_list";
    }

    /* ~~~~~~~~~~~ Dev Admin Page ~~~~~~~~~~~ */
    @GetMapping("/devAdminPage")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getDevAdminPage(Model model) {
        //todo adauga campuri si fa frumos devAdminPage
        return "pages/layer 4/info pages/developer/devAdminPage";
    }

    /* ~~~~~~~~~~~ Register New Admin/Assistant ~~~~~~~~~~~ */
//    @PostMapping
//    @PreAuthorize("hasAuthority('student:write')")
//    public void registerNewStudent(@RequestBody Student student)
//    {
//        System.out.println("POST: registerNewStudent");
//        studentService.addNewStudent(student);
//    }

    /* ~~~~~~~~~~~ Register New Student ~~~~~~~~~~~ */
//    @PostMapping
//    @PreAuthorize("hasAuthority('student:write')")
//    public void registerNewStudent(@RequestBody Student student)
//    {
//        System.out.println("POST: registerNewStudent");
//        studentService.addNewStudent(student);
//    }

    /* ~~~~~~~~~~~ DELETE Student ~~~~~~~~~~~ */
    @GetMapping(path = "/students/delete/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String deleteStudent(@PathVariable("studentId") Long id) {
        studentService.deleteStudent(id);
        studentAccountService.deleteStudent(id);

        return "redirect:/admin/students";
    }

    /* ~~~~~~~~~~~ Get Student knowing ID ~~~~~~~~~~~ */
    @GetMapping(path = "/students/edit/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String editStudent(@PathVariable("studentId") Long studentId, Model model) {
        model.addAttribute("selectedStudentById", studentService.editStudent(studentId));

        return "pages/layer 4/students table/crud students/update_student";
    }

    /* ~~~~~~~~~~~ Update Student and Redirect to Student List ~~~~~~~~~~~ */
    @PostMapping(path = "/students/update/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String updateStudent(@PathVariable("studentId") Long studentId, Student newStudent) {
        //campuri comune modificabile: nume, prenume
        //campuri comune nemodificabile: cnp, zi_de_nastere
        //campuri necomune student: an, grupa, serie, judet
        studentService.updateStudent(studentId, newStudent);
        studentAccountService.updateStudent(studentId, newStudent);

        return "redirect:/admin/students";
    }

    /* ~~~~~~~~~~~ Update Student and Redirect to Student List ~~~~~~~~~~~ */
    @RequestMapping(path = "/students/setFlag/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String updateFlag(@PathVariable("studentId") Long studentId) {
        studentService.updateFlag(studentId);

        return "redirect:/admin/students";
    }
}
