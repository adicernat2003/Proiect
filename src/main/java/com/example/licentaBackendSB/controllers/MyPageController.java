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

    @RequestMapping(value = {"/camere-update/{studentId}/{anUniversitar}", "/friend-tokens-update/{studentId}/{anUniversitar}"},
            method = RequestMethod.POST, params = "action=cancel")
    public String redirectToMyPage(@PathVariable("anUniversitar") String anUniversitar) {
        return studentService.redirectToMyPage(anUniversitar);
    }

    @GetMapping(path = "/camine-edit/{studentId}")
    public String editCaminePreferate(@PathVariable("studentId") Long studentId, Model model) {
        return studentService.editOptiuniCaminePreferate(studentId, model);
    }

    @RequestMapping(value = "/camine-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=save")
    public String updateOptiuniCaminePreferate(@PathVariable("studentId") Long studentId,
                                               @PathVariable("anUniversitar") String anUniversitar,
                                               StudentDto newStudent) {
        return studentService.updateCaminePreferate(studentId, anUniversitar, newStudent);
    }

    @RequestMapping(value = "/camine-update/delete-clear-camin-preferat/{studentId}/{anUniversitar}/{option}")
    public String clearCaminPreferat(@PathVariable("studentId") Long studentId,
                                     @PathVariable("anUniversitar") String anUniversitar,
                                     @PathVariable("option") String indexOptiuneString) {
        return studentService.clearCaminPreferat(studentId, indexOptiuneString);
    }

    @RequestMapping(path = "/camine-clear/{studentId}/{anUniversitar}")
    public String clearCaminePreferate(@PathVariable("studentId") Long studentId,
                                       @PathVariable("anUniversitar") String anUniversitar) {
        return studentService.clearCaminePreferate(studentId, anUniversitar);
    }

    @GetMapping(path = "/camere-edit/{studentId}")
    public String editCamerePreferate(@PathVariable("studentId") Long studentId, Model model) {
        return studentService.editOptiuniCamereCamin(studentId, model);
    }

    @RequestMapping(value = "/camere-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=save")
    public String updateOptiuniCamere(@PathVariable("studentId") Long studentId,
                                      @PathVariable("anUniversitar") String anUniversitar,
                                      StudentDto newStudent) {
        return studentService.updateOptiuniCamere(studentId, anUniversitar, newStudent);
    }

    @RequestMapping(value = "/camere-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=add-more")
    public String addAnotherOptiuneCamera(@PathVariable("studentId") Long studentId,
                                          @PathVariable("anUniversitar") String anUniversitar,
                                          StudentDto newStudent) {
        return studentService.addAnotherOptiuneCamera(studentId, anUniversitar, newStudent);
    }

    @RequestMapping(value = "/camere-update/delete-optiune-camera/{studentId}/{anUniversitar}/{option}")
    public String clearCameraPreferata(@PathVariable("studentId") Long studentId,
                                       @PathVariable("anUniversitar") String anUniversitar,
                                       @PathVariable("option") String indexOptiuneString) {
        return studentService.clearCameraPreferata(studentId, indexOptiuneString, anUniversitar);
    }

    @RequestMapping(path = "/camere-clear/{studentId}/{anUniversitar}")
    public String clearCamerePreferate(@PathVariable("studentId") Long studentId,
                                       @PathVariable("anUniversitar") String anUniversitar) {
        return studentService.clearCamerePreferate(studentId, anUniversitar);
    }

    @GetMapping(path = "/friend-tokens-edit/{studentId}")
    public String editFriendTokens(@PathVariable("studentId") Long studentId, Model model) {
        return studentService.editFriendTokens(studentId, model);
    }

    @RequestMapping(value = "/friend-tokens-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=save")
    public String updateFriendTokens(@PathVariable("studentId") Long studentId,
                                     @PathVariable("anUniversitar") String anUniversitar,
                                     StudentDto newStudent) {
        return studentService.updateFriendTokens(studentId, anUniversitar, newStudent);
    }

    @RequestMapping(value = "/friend-tokens-update/{studentId}/{anUniversitar}", method = RequestMethod.POST, params = "action=add-more")
    public String addAnotherFriend(@PathVariable("studentId") Long studentId,
                                   @PathVariable("anUniversitar") String anUniversitar,
                                   StudentDto newStudent) {
        return studentService.addAnotherFriend(studentId, anUniversitar, newStudent);
    }

    @RequestMapping(value = "/friend-tokens-update/delete-friend-token/{studentId}/{anUniversitar}/{option}")
    public String deleteFriendToken(@PathVariable("studentId") Long studentId,
                                    @PathVariable("anUniversitar") String anUniversitar,
                                    @PathVariable("option") String indexOptiuneString) {
        return studentService.deleteFriendToken(studentId, indexOptiuneString);
    }

    @RequestMapping(path = "/friend-tokens-clear/{studentId}/{anUniversitar}")
    public String clearFriendTokens(@PathVariable("studentId") Long studentId,
                                    @PathVariable("anUniversitar") String anUniversitar) {
        return studentService.clearFriendTokens(studentId, anUniversitar);
    }

}
