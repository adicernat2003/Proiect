package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.managers.Manager;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.others.Validator;
import com.example.licentaBackendSB.repositories.CaminRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import com.example.licentaBackendSB.services.SessionService;
import com.example.licentaBackendSB.services.StudentAccountService;
import com.example.licentaBackendSB.services.StudentCaminService;
import com.example.licentaBackendSB.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Optional;

@Controller
@RequestMapping(path = "/student/mypage")
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class MyPageController {

    //Fields
    private final StudentService studentService;
    private final StudentAccountService studentAccountService;
    private final StudentCaminService studentCaminService;
    private final StudentConverter studentConverter;
    private final Manager manager;
    private final SessionService sessionService;
    private final CaminRepository caminRepository;
    private final StudentRepository studentRepository;

    /* ~~~~~~~~~~~ Get MyPage View ~~~~~~~~~~~ */
    @GetMapping("/{anUniversitar}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getMyPage(@PathVariable String anUniversitar, Model model) {
        LoggedAccount loggedAccount = new LoggedAccount();

        if (loggedAccount.checkIfStandardAccLogged()) {
            model.addAttribute("devUsernameAccount", loggedAccount.getLoggedUsername());
        } else {
            StudentAccount loggedStudentAccount = studentAccountService.getLoggedStudentAccount();
            //query call in db to get info of logged student
            Student infoStudent = studentService.findStudentByNameAndSurnameAndAnUniversitar(loggedStudentAccount, Integer.parseInt(anUniversitar));

            //getting info about logged acc (credentials) && student info
            model.addAttribute("loggedStudentAccount", loggedStudentAccount);
            model.addAttribute("infoStudent", infoStudent);
            //checkup in case we log in with a dev account
            model.addAttribute("isDevAcc", loggedAccount.checkIfStandardAccLogged().toString());

            model.addAttribute("selectedYears", manager.getListOfYearsForStudentPage(infoStudent, Integer.parseInt(anUniversitar)));
            model.addAttribute("anCurent", anUniversitar);
            model.addAttribute("anUniversitar", anUniversitar);

            Optional<Student> secondStudent = studentService.findStudentByMyToken(infoStudent.getFriendToken());
            if (secondStudent.isPresent()) {
                model.addAttribute("yourFriend", secondStudent);
            }
        }
        return "pages/layer 4/info pages/student/mypage";
    }

    @RequestMapping
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getSelectedYearPage(@RequestParam(required = false, name = "year") String year) {
        sessionService.getNewSession(Integer.parseInt(year), null);
        return "redirect:/student/mypage/" + year;
    }

    /* ~~~~~~~~~~~ Get Student knowing the ID for setting the Friend Token ~~~~~~~~~~~ */
    @GetMapping(path = "/ft-edit/{studentId}/{anUniversitar}")
    public String editFriendToken(@PathVariable("studentId") Long studentId,
                                  @PathVariable("anUniversitar") String anUniversitar,
                                  Model model) {
        Student selectedStudent = studentService.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("anUniversitar", anUniversitar);

        if (selectedStudent.getFriendToken() == null) {
            return "pages/layer 4/info pages/student/crud mypage/update_friendToken";
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    /* ~~~~~~~~~~~ Update Student Friend Token and Redirect to MyPage ~~~~~~~~~~~ */
    @PostMapping(path = "/ft-update/{studentId}/{anUniversitar}")
    public String updateFriendToken(@PathVariable("studentId") Long studentId,
                                    @PathVariable("anUniversitar") String anUniversitar,
                                    StudentDto newStudent,
                                    Model model) {
        String isError = studentService.validateFriendToken(newStudent);
        if (isError.equals("All good!")) {
            //Kid#1 preia friendTokenul introdus in frontend
            studentService.updateFriendToken(studentId, newStudent);
            //Cautam Kid#1 dupa id
            Student firstStudent = studentService.getStudentById(studentId);

            //Verificam daca camin e !null
            if (firstStudent.getCamin_preferat() != null) {
                //daca nu e null, dam update in tabelul pt caminul respectiv
                Camin camin = caminRepository.findCaminByNumeCaminAndAnUniversitar(firstStudent.getCamin_preferat(), Integer.parseInt(anUniversitar)).get();
                studentCaminService.updateFriendTokenOfStudentInCamin(studentConverter.convertStudentToStudentCamin(firstStudent, camin), camin, Integer.parseInt(anUniversitar));
            }
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    /* ~~~~~~~~~~~ Clear FriendToken and Update with null and Redirect to MyPage ~~~~~~~~~~~ */
    @RequestMapping(path = "/ft-clear/{studentId}/{anUniversitar}")
    public String clearFriendToken(@PathVariable("studentId") Long studentId,
                                   @PathVariable("anUniversitar") String anUniversitar) {
        //Preluam studentul actual adica Kid#1 stiind Id-ul
        Student firstStudent = studentService.getStudentById(studentId);
        if (firstStudent.getFriendToken() != null /*&& !secondStudent.get().getFriendToken().equals("null")*/) {
            //Setam local "null" la Kid#1
            firstStudent.setFriendToken(null);
            //Updatam in db Kid#1 cu campul friendToken din Kid#1 local
            studentService.clearFriendToken(firstStudent.getId(), firstStudent);

            //Verificam daca camin e !null
            if (firstStudent.getCamin_preferat() != null) {
                Camin camin = caminRepository.findCaminByNumeCaminAndAnUniversitar(firstStudent.getCamin_preferat(), Integer.parseInt(anUniversitar)).get();
                //daca nu e null, dam update in tabelul pt caminul respectiv
                studentCaminService.updateFriendTokenOfStudentInCamin(studentConverter.convertStudentToStudentCamin(firstStudent, camin), camin, Integer.parseInt(anUniversitar));
            }
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    /* ~~~~~~~~~~~ Get Student knowing the ID for setting the Camin ~~~~~~~~~~~ */
    @GetMapping(path = "/camin-edit/{studentId}")
    public String editCamin(@PathVariable("studentId") Long studentId, Model model) {
        Student selectedStudent = studentService.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        if (selectedStudent.getCamin_preferat() == null) {
            return "pages/layer 4/info pages/student/crud mypage/update_camin";
        }
        return "redirect:/student/mypage/" + selectedStudent.getAnUniversitar();
    }

    /* ~~~~~~~~~~~ Update Student Camin and Redirect to MyPage ~~~~~~~~~~~ */
    @PostMapping(path = "/camin-update/{studentId}/{anUniversitar}")
    public String updateCamin(@PathVariable("studentId") Long studentId,
                              @PathVariable("anUniversitar") String anUniversitar,
                              StudentDto newStudent) {
        if (Validator.checkCaminSpelling(newStudent.getCamin_preferat())) {
            studentService.updateCamin(studentId, newStudent, anUniversitar);
            Camin camin = caminRepository.findCaminByNumeCaminAndAnUniversitar(newStudent.getCamin_preferat(), Integer.parseInt(anUniversitar)).get();
            //Preluam studentul care isi adauga camin
            Student selectedStudent = studentService.getStudentById(studentId);
            //Introducem studentul local cu caminul modificat in tabelul corespunzator
            studentCaminService.introduceNewStudentCamin(studentConverter.convertStudentToStudentCamin(selectedStudent, camin));
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    /* ~~~~~~~~~~~ Clear Camin and Update with null and Redirect to MyPage ~~~~~~~~~~~ */
    @RequestMapping(path = "/camin-clear/{studentId}/{anUniversitar}")
    public String clearCamin(@PathVariable("studentId") Long studentId,
                             @PathVariable("anUniversitar") String anUniversitar) {
        Student selectedStudent = studentService.getStudentById(studentId);
        if (selectedStudent.getCamin_preferat() != null) {
            Camin camin = caminRepository.findCaminByNumeCaminAndAnUniversitar(selectedStudent.getCamin_preferat(), Integer.parseInt(anUniversitar)).get();
            studentCaminService.deleteStudentInCamin(studentConverter.convertStudentToStudentCamin(selectedStudent, camin), anUniversitar);
            //Intai stergem din tabel persoana respectiva si dupa ii stergem optiunea aleasa
            studentService.clearCamin(selectedStudent);
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    @GetMapping(path = "/camere-edit/{studentId}")
    public String editOptiuniCamereCamin(@PathVariable("studentId") Long studentId, Model model) {
        Student selectedStudent = studentService.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("listOfCamere", manager.getListaOptiuniCamereForStudent(selectedStudent));
        model.addAttribute("firstOption", selectedStudent.getNumarLocuriCamera().size() > 0 ? selectedStudent.getNumarLocuriCamera().get(0) : null);
        model.addAttribute("secondOption", selectedStudent.getNumarLocuriCamera().size() > 1 ? selectedStudent.getNumarLocuriCamera().get(1) : null);
        model.addAttribute("thirdOption", selectedStudent.getNumarLocuriCamera().size() > 2 ? selectedStudent.getNumarLocuriCamera().get(2) : null);
        model.addAttribute("fourthOption", selectedStudent.getNumarLocuriCamera().size() > 3 ? selectedStudent.getNumarLocuriCamera().get(3) : null);
        return "pages/layer 4/info pages/student/crud mypage/update_optiuni_camere";
    }

    @RequestMapping(value = "/camere-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=save")
    public String updateOptiuniCamere(@PathVariable("studentId") Long studentId,
                                      @PathVariable("anUniversitar") String anUniversitar,
                                      StudentDto newStudent) {
        Student student = studentService.getStudentById(studentId);
        if (newStudent.getNumarLocuriCamera() != null) {
            newStudent.getNumarLocuriCamera().forEach(camera -> student.getNumarLocuriCamera().add(camera));
            studentRepository.save(student);
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    @RequestMapping(value = "/camere-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=add-more")
    public String addMoreOptiuni(@PathVariable("studentId") Long studentId,
                                 @PathVariable("anUniversitar") String anUniversitar,
                                 StudentDto newStudent) {
        Student student = studentService.getStudentById(studentId);
        if (newStudent.getNumarLocuriCamera() != null) {
            newStudent.getNumarLocuriCamera().forEach(camera -> student.getNumarLocuriCamera().add(camera));
            studentRepository.save(student);
            return "redirect:/student/mypage/" + "camere-edit/" + studentId;
        } else {
            return "redirect:/student/mypage/" + anUniversitar;
        }
    }

    @RequestMapping(value = "/camere-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=cancel")
    public String redirectToMyPage(@PathVariable("anUniversitar") String anUniversitar) {
        return "redirect:/student/mypage/" + anUniversitar;
    }

    @RequestMapping(value = "/camere-update/delete-optiune-camera/{studentId}/{anUniversitar}/{option}")
    public String deleteOptiuneCamera(@PathVariable("studentId") Long studentId,
                                      @PathVariable("anUniversitar") String anUniversitar,
                                      @PathVariable("option") String indexOptiuneString) {
        Student student = studentService.getStudentById(studentId);
        int indexOptiuneInteger = Integer.parseInt(indexOptiuneString);
        student.getNumarLocuriCamera().remove(indexOptiuneInteger);
        studentRepository.save(student);
        return "redirect:/student/mypage/" + "camere-edit/" + studentId;
    }

    @RequestMapping(path = "/camere-clear/{studentId}/{anUniversitar}")
    public String clearOptiuniCamere(@PathVariable("studentId") Long studentId,
                                     @PathVariable("anUniversitar") String anUniversitar) {
        Student student = studentService.getStudentById(studentId);
        student.getNumarLocuriCamera().clear();
        studentRepository.save(student);
        return "redirect:/student/mypage/" + anUniversitar;
    }
}
