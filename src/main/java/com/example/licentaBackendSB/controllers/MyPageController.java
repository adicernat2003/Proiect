package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.services.SessionService;
import com.example.licentaBackendSB.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/student/mypage")
@RequiredArgsConstructor
public class MyPageController {

    //Fields
    private final StudentService studentService;
    private final SessionService sessionService;

    /* ~~~~~~~~~~~ Get MyPage View ~~~~~~~~~~~ */
    @GetMapping("/{anUniversitar}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getMyPage(@PathVariable String anUniversitar, Model model) {
        return studentService.getMyPage(anUniversitar, model);
    }

    @RequestMapping
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getSelectedYearPage(@RequestParam(required = false, name = "year") String year) {
        sessionService.getNewSession(Integer.parseInt(year), null);
        return "redirect:/student/mypage/" + year;
    }

    @PostMapping(value = {"/camine-update/{studentId}/{anUniversitar}", "/friend-tokens-update/{studentId}/{anUniversitar}",
            "/camere-update/{studentId}/{anUniversitar}"}, params = "action=cancel")
    public String redirectToMyPage(@PathVariable("anUniversitar") String anUniversitar) {
        return studentService.redirectToMyPage(anUniversitar);
    }

    @GetMapping(path = "/camine-edit/{studentId}")
    public String editCaminePreferate(@PathVariable("studentId") Long studentId, Model model) {
        return studentService.editCaminePreferate(studentId, model);
    }

    @RequestMapping(value = "/camine-update/delete-clear-camin-preferat/{studentId}/{anUniversitar}/{option}")
    public String clearCaminPreferat(@PathVariable("studentId") Long studentId,
                                     @PathVariable("anUniversitar") String anUniversitar,
                                     @PathVariable("option") String indexOptiuneString) {
        //return studentService.clearCaminePreferate(studentId, indexOptiuneString, anUniversitar, false);
        return null;
    }

    @RequestMapping(path = "/camine-clear/{studentId}/{anUniversitar}")
    public String clearCaminePreferate(@PathVariable("studentId") Long studentId,
                                       @PathVariable("anUniversitar") String anUniversitar) {
        //return studentService.clearCaminePreferate(studentId, null, anUniversitar, true);
        return null;
    }

    @GetMapping(path = "/camere-edit/{studentId}")
    public String editCamerePreferate(@PathVariable("studentId") Long studentId, Model model) {
        return studentService.editOptiuniCamereCamin(studentId, model);
    }

    @RequestMapping(value = "/camere-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=save")
    public String updateOptiuniCamere(@PathVariable("studentId") Long studentId,
                                      @PathVariable("anUniversitar") String anUniversitar,
                                      StudentDto newStudent) {
        return studentService.updateOptiuniCamere(studentId, anUniversitar, newStudent, false);
    }

    @RequestMapping(value = "/camere-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=add-more")
    public String addAnotherOptiuneCamera(@PathVariable("studentId") Long studentId,
                                          @PathVariable("anUniversitar") String anUniversitar,
                                          StudentDto newStudent) {
        return studentService.updateOptiuniCamere(studentId, anUniversitar, newStudent, true);
    }

    @RequestMapping(value = "/camere-update/delete-optiune-camera/{studentId}/{anUniversitar}/{option}")
    public String clearCameraPreferata(@PathVariable("studentId") Long studentId,
                                       @PathVariable("anUniversitar") String anUniversitar,
                                       @PathVariable("option") String numarCamera) {
        return studentService.clearCamerePreferate(studentId, numarCamera, anUniversitar, false);
    }

    @RequestMapping(path = "/camere-clear/{studentId}/{anUniversitar}")
    public String clearCamerePreferate(@PathVariable("studentId") Long studentId,
                                       @PathVariable("anUniversitar") String anUniversitar) {
        return studentService.clearCamerePreferate(studentId, null, anUniversitar, true);
    }

    @GetMapping(path = "/friend-tokens-edit/{studentId}")
    public String editFriendTokens(@PathVariable("studentId") Long studentId, Model model) {
        return studentService.editFriendTokens(studentId, model);
    }

    @RequestMapping(value = "/friend-tokens-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=save")
    public String updateFriendTokens(@PathVariable("studentId") Long studentId,
                                     @PathVariable("anUniversitar") String anUniversitar,
                                     StudentDto newStudent) {
        return studentService.updateFriendTokens(studentId, anUniversitar, newStudent, false);
    }

    @RequestMapping(value = "/friend-tokens-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=add-more")
    public String addAnotherFriend(@PathVariable("studentId") Long studentId,
                                   @PathVariable("anUniversitar") String anUniversitar,
                                   StudentDto newStudent) {
        return studentService.updateFriendTokens(studentId, anUniversitar, newStudent, true);
    }

    @RequestMapping(value = "/friend-tokens-update/delete-friend-token/{studentId}/{anUniversitar}/{option}")
    public String clearFriendToken(@PathVariable("studentId") Long studentId,
                                   @PathVariable("anUniversitar") String anUniversitar,
                                   @PathVariable("option") String indexOptiuneString) {
        return studentService.clearFriendTokens(studentId, indexOptiuneString, anUniversitar, false);
    }

    @RequestMapping(path = "/friend-tokens-clear/{studentId}/{anUniversitar}")
    public String clearFriendTokens(@PathVariable("studentId") Long studentId,
                                    @PathVariable("anUniversitar") String anUniversitar) {
        return studentService.clearFriendTokens(studentId, null, anUniversitar, true);
    }

}
