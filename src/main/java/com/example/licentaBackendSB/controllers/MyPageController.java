package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.others.Validator;
import com.example.licentaBackendSB.services.StudentAccountService;
import com.example.licentaBackendSB.services.StudentCaminService;
import com.example.licentaBackendSB.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping(path = "/student/mypage")
@RequiredArgsConstructor
public class MyPageController {

    //Fields
    private final StudentService studentService;
    private final StudentAccountService studentAccountService;
    private final StudentCaminService studentCaminService;
    private final StudentConverter studentConverter;

    /* ~~~~~~~~~~~ Get MyPage View ~~~~~~~~~~~ */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getMyPage(Model model) {
        LoggedAccount loggedAccount = new LoggedAccount();

        if (loggedAccount.checkIfStandardAccLogged()) {
            model.addAttribute("devUsernameAccount", loggedAccount.getLoggedUsername());
        } else {
            StudentAccount loggedStudentAccount = studentAccountService.getLoggedStudentAccount();
            //query call in db to get info of logged student
            Student infoStudent = studentService.findStudentByNameAndSurname(loggedStudentAccount);

            //getting info about logged acc (credentials) && student info
            model.addAttribute("loggedStudentAccount", loggedStudentAccount);
            model.addAttribute("infoStudent", infoStudent);
            //checkup in case we log in with a dev account
            model.addAttribute("isDevAcc", loggedAccount.checkIfStandardAccLogged().toString());

            Optional<Student> secondStudent = studentService.findStudentByMyToken(infoStudent.getFriendToken());
            if (secondStudent.isPresent()) {
                model.addAttribute("yourFriend", secondStudent);
            }
        }
        return "pages/layer 4/info pages/student/mypage";
    }

    /* ~~~~~~~~~~~ Get Student knowing the ID for setting the Friend Token ~~~~~~~~~~~ */
    @GetMapping(path = "/ft-edit/{studentId}")
    public String editFriendToken(@PathVariable("studentId") Long studentId, Model model) {
        Student selectedStudent = studentService.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);

        if (selectedStudent.getFriendToken().equals("null")) {
            return "pages/layer 4/info pages/student/crud mypage/update_friendToken";
        }
        return "redirect:/student/mypage";
    }

    /* ~~~~~~~~~~~ Update Student Friend Token and Redirect to MyPage ~~~~~~~~~~~ */
    @PostMapping(path = "/ft-update/{studentId}")
    public String updateFriendToken(@PathVariable("studentId") Long studentId,
                                    StudentDto newStudent,
                                    Model model) {
        String isError = studentService.validateFriendToken(newStudent);
        if (isError.equals("All good!")) {
            //Kid#1 preia friendTokenul introdus in frontend
            studentService.updateFriendToken(studentId, newStudent);
            //Cautam Kid#1 dupa id
            Student firstStudent = studentService.getStudentById(studentId);

            //Verificam daca camin e !null
            if (!firstStudent.getCamin_preferat().equals("null")) {
                //daca nu e null, dam update in tabelul pt caminul respectiv
                studentCaminService.updateFriendTokenOfStudentInCamin(studentConverter.convertStudentToStudentCamin(firstStudent,
                        firstStudent.getCamin_preferat()), firstStudent.getCamin_preferat());
            }
        }
        return "redirect:/student/mypage";
    }

    /* ~~~~~~~~~~~ Clear FriendToken and Update with null and Redirect to MyPage ~~~~~~~~~~~ */
    @RequestMapping(path = "/ft-clear/{studentId}")
    public String clearFriendToken(@PathVariable("studentId") Long studentId) {
        //Preluam studentul actual adica Kid#1 stiind Id-ul
        Student firstStudent = studentService.getStudentById(studentId);
        if (!firstStudent.getFriendToken().equals("null") /*&& !secondStudent.get().getFriendToken().equals("null")*/) {
            //Setam local "null" la Kid#1
            firstStudent.setFriendToken("null");
            //Updatam in db Kid#1 cu campul friendToken din Kid#1 local
            studentService.clearFriendToken(firstStudent.getId(), firstStudent);

            //Verificam daca camin e !null
            if (!firstStudent.getCamin_preferat().equals("null")) {
                //daca nu e null, dam update in tabelul pt caminul respectiv
                studentCaminService.updateFriendTokenOfStudentInCamin(studentConverter.convertStudentToStudentCamin(firstStudent,
                        firstStudent.getCamin_preferat()), firstStudent.getCamin_preferat());
            }
        }
        return "redirect:/student/mypage";
    }

    /* ~~~~~~~~~~~ Get Student knowing the ID for setting the Camin ~~~~~~~~~~~ */
    @GetMapping(path = "/camin-edit/{studentId}")
    public String editCamin(@PathVariable("studentId") Long studentId, Model model) {
        Student selectedStudent = studentService.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);

        if (selectedStudent.getCamin_preferat().equals("null")) {
            return "pages/layer 4/info pages/student/crud mypage/update_camin";
        }
        return "redirect:/student/mypage";
    }

    /* ~~~~~~~~~~~ Update Student Camin and Redirect to MyPage ~~~~~~~~~~~ */
    @PostMapping(path = "/camin-update/{studentId}")
    public String updateCamin(@PathVariable("studentId") Long studentId,
                              StudentDto newStudent,
                              Model model) {
        if (Validator.checkCaminSpelling(newStudent.getCamin_preferat())) {
            studentService.updateCamin(studentId, newStudent);

            //Preluam studentul care isi adauga camin
            Student selectedStudent = studentService.getStudentById(studentId);
            //Modificam local din ce avea la camin cu ce a ales
            selectedStudent.setCamin_preferat(newStudent.getCamin_preferat());
            //Introducem studentul local cu caminul modificat in tabelul corespunzator
            studentCaminService.introduceNewStudentCamin(studentConverter.convertStudentToStudentCamin(selectedStudent, selectedStudent.getCamin_preferat()));
        }
        return "redirect:/student/mypage";
    }

    /* ~~~~~~~~~~~ Clear Camin and Update with null and Redirect to MyPage ~~~~~~~~~~~ */
    @RequestMapping(path = "/camin-clear/{studentId}")
    public String clearCamin(@PathVariable("studentId") Long studentId) {
        Student selectedStudent = studentService.getStudentById(studentId);
        if (!selectedStudent.getCamin_preferat().equals("null")) {
            studentCaminService.deleteStudentInCamin(studentConverter.convertStudentToStudentCamin(selectedStudent,
                    selectedStudent.getCamin_preferat()), selectedStudent.getCamin_preferat());
            //Intai stergem din tabel persoana respectiva si dupa ii stergem optiunea aleasa
            selectedStudent.setCamin_preferat("null");
            studentService.clearCamin(selectedStudent.getId(), selectedStudent);
        }
        return "redirect:/student/mypage";
    }
}
