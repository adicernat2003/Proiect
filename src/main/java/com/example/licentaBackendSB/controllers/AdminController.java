package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.managers.Manager;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.services.AccommodationService;
import com.example.licentaBackendSB.services.SessionService;
import com.example.licentaBackendSB.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.example.licentaBackendSB.enums.Session.STUDENT;

@Controller
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StudentService studentService;
    private final StudentConverter studentConverter;
    private final SessionService sessionService;
    private final AccommodationService accommodationService;
    private final Manager manager;

    @RequestMapping("/insert-preferinte/{anUniversitar}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String insertRandomPreferinteForEachStudent(@PathVariable String anUniversitar, Model model) {
        studentService.insertRandomPreferinteForEachStudent(anUniversitar);
        return this.getStudents(anUniversitar, model);
    }

    @RequestMapping("/insert-undesired-camine/{anUniversitar}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String insertRandomUndesiredCamineForEachStudent(@PathVariable String anUniversitar, Model model) {
        studentService.insertRandomUndesiredCamineForEachStudent(anUniversitar);
        return this.getStudents(anUniversitar, model);
    }

    @RequestMapping("/make-friends/{anUniversitar}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String makeFriends(@PathVariable String anUniversitar, Model model) {
        studentService.makeFriends(anUniversitar);
        return this.getStudents(anUniversitar, model);
    }

    @RequestMapping("/accommodate/{anUniversitar}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String accommodateStudents(@PathVariable String anUniversitar, Model model) {
        accommodationService.accommodateStudents(Integer.parseInt(anUniversitar));
        return this.getStudents(anUniversitar, model);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getAdminView(Model model) {
        LoggedAccount loggedAccount = new LoggedAccount();
        model.addAttribute("loggedUsername", loggedAccount.getLoggedUsername());
        model.addAttribute("isDevAcc", loggedAccount.checkIfStandardAccLogged().toString());

        return "pages/layer 3/admin";
    }

    @GetMapping("/students/{anUniversitar}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getStudents(@PathVariable String anUniversitar, Model model) {
        model.addAttribute("listOfStudents", sessionService.getNewSession(Integer.parseInt(anUniversitar), STUDENT));
        model.addAttribute("selectedYears", manager.getListOfYears(Integer.parseInt(anUniversitar)));
        model.addAttribute("anCurent", anUniversitar);
        model.addAttribute("anUniversitar", anUniversitar);
        model.addAttribute("isAdmin", "admin");
        return "pages/layer 4/students table/students_list";
    }

    @RequestMapping("/students")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getSelectedYearStudents(@RequestParam(required = false, name = "year") String year) {
        return "redirect:/admin/students/" + year;
    }

    @GetMapping("/devAdminPage")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getDevAdminPage(Model model) {
        //todo adauga campuri si fa frumos devAdminPage
        return "pages/layer 4/info pages/developer/devAdminPage";
    }

    @GetMapping(path = "/students/{anUniversitar}/delete/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String deleteStudent(@PathVariable(value = "anUniversitar") String anUniversitar,
                                @PathVariable(value = "studentId") Long id) {
        studentService.deleteStudent(id);

        return "redirect:/admin/students/" + anUniversitar;
    }

    @GetMapping(path = "/students/{anUniversitar}/edit/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String editStudent(@PathVariable(value = "anUniversitar") String anUniversitar,
                              @PathVariable("studentId") Long studentId, Model model) {
        Student student = studentService.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(student);
        model.addAttribute("selectedStudentById", studentDto);

        return "pages/layer 4/students table/crud students/update_student";
    }

    @PostMapping(path = "/students/{anUniversitar}/update/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String updateStudent(@PathVariable(value = "anUniversitar") String anUniversitar,
                                @PathVariable("studentId") Long studentId,
                                StudentDto newStudent) {
        //campuri comune modificabile: nume, prenume
        //campuri comune nemodificabile: cnp, zi_de_nastere
        //campuri necomune student: an, grupa, serie, judet
        studentService.updateStudent(studentId, newStudent);

        return "redirect:/admin/students/" + anUniversitar;
    }

    @RequestMapping(path = "/students/{anUniversitar}/setFlag/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String updateFlag(@PathVariable(value = "anUniversitar") String anUniversitar,
                             @PathVariable("studentId") Long studentId) {
        studentService.updateFlag(studentId);

        return "redirect:/admin/students/" + anUniversitar;
    }
}
