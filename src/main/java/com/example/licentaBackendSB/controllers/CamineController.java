package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.converters.CaminConverter;
import com.example.licentaBackendSB.managers.Manager;
import com.example.licentaBackendSB.model.dtos.CaminDto;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.services.CaminService;
import com.example.licentaBackendSB.services.SessionService;
import com.example.licentaBackendSB.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.example.licentaBackendSB.constants.Constants.*;
import static com.example.licentaBackendSB.enums.Session.CAMIN;

@Controller
@RequestMapping(path = "/admin/camine")
@RequiredArgsConstructor
public class CamineController {

    //Fields
    private final CaminService caminService;
    private final CaminConverter caminConverter;
    private final Manager manager;
    private final SessionService sessionService;
    private final StudentService studentService;

    /* ~~~~~~~~~~~ Get Camine View ~~~~~~~~~~~ */
    @GetMapping("/{anUniversitar}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getCaminePage(@PathVariable String anUniversitar, Model model) {
        model.addAttribute("listOfCamine", sessionService.getNewSession(Integer.parseInt(anUniversitar), CAMIN));
        model.addAttribute("selectedYears", manager.getListOfYears(Integer.parseInt(anUniversitar)));
        model.addAttribute("anCurent", anUniversitar);
        model.addAttribute("leuA", caminService.getCaminByNumeCaminAndAnUniversitar(LEU_A, Integer.parseInt(anUniversitar)));
        model.addAttribute("leuC", caminService.getCaminByNumeCaminAndAnUniversitar(LEU_C, Integer.parseInt(anUniversitar)));
        model.addAttribute("p20", caminService.getCaminByNumeCaminAndAnUniversitar(P20, Integer.parseInt(anUniversitar)));
        model.addAttribute("p23", caminService.getCaminByNumeCaminAndAnUniversitar(P23, Integer.parseInt(anUniversitar)));

        return "pages/layer 4/camine/camine_page";
    }

    @RequestMapping("/schimbaAn")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getSelectedYearCamine(@RequestParam(required = false, name = "year") String year) {
        return "redirect:/admin/camine/" + year;
    }

    @GetMapping("/{anUniversitar}/students/{numeCamin}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getStudents(@PathVariable String anUniversitar,
                              @PathVariable String numeCamin,
                              Model model) {
        model.addAttribute("listOfStudents", studentService.getStudentsByCaminAndAnUniversitar(numeCamin.replace('_', ' '), anUniversitar));
        return "pages/layer 4/camine/tables/studentCaminList";
    }

    /* ~~~~~~~~~~~ Get Camin knowing ID ~~~~~~~~~~~ */
    @GetMapping(path = "/edit/{caminId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String editCamin(@PathVariable("caminId") Long caminId, Model model) {
        Camin camin = caminService.getCaminById(caminId);
        CaminDto caminDto = caminConverter.mapCaminEntityToDto(camin);
        model.addAttribute("selectedCaminById", caminDto);

        return "pages/layer 4/camine/crud camine/update_info_camin";
    }

    /* ~~~~~~~~~~~ Update Camin and Redirect to Camine Page ~~~~~~~~~~~ */
    @PostMapping(path = "/update/{caminId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String updateCamin(@PathVariable("caminId") Long caminId, CaminDto newCamin) {
        return "redirect:/admin/camine/" + caminService.updateCamin(caminId, newCamin);
    }

    /* ~~~~~~~~~~~ Clear Fields and Update with 0 and Redirect to Camine Page ~~~~~~~~~~~ */
    @GetMapping(path = "/clear/{caminId}")
    public String clearCamin(@PathVariable("caminId") Long caminId) {
        return "redirect:/admin/camine/" + caminService.clearCamin(caminId);
    }
}
