package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.entities.Camin;
import com.example.licentaBackendSB.services.CaminService;
import com.example.licentaBackendSB.services.StudentCaminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.example.licentaBackendSB.constants.Constants.*;

@Controller
@RequestMapping(path = "/admin/camine")
@RequiredArgsConstructor
public class CamineController {

    //Fields
    private final CaminService caminService;
    private final StudentCaminService studentCaminService;

    /* ~~~~~~~~~~~ Get Camine View ~~~~~~~~~~~ */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getCaminePage(Model model) {
        model.addAttribute("listOfCamine", caminService.getCamine());
        model.addAttribute("leuA", caminService.getCaminByNumeCamin(LEU_A));
        model.addAttribute("leuC", caminService.getCaminByNumeCamin(LEU_C));
        model.addAttribute("p20", caminService.getCaminByNumeCamin(P20));
        model.addAttribute("p23", caminService.getCaminByNumeCamin(P23));

        return "pages/layer 4/camine/camine_page";
    }

    @GetMapping("/{numeCamin}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getStudents(@PathVariable String numeCamin, Model model) {
        model.addAttribute("listOfStudents", studentCaminService.getStudents(numeCamin));

        return "pages/layer 4/camine/tables/studentCaminList";
    }

    /* ~~~~~~~~~~~ Get Camin knowing ID ~~~~~~~~~~~ */
    @GetMapping(path = "/edit/{caminId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String editCamin(@PathVariable("caminId") Long caminId, Model model) {
        model.addAttribute("selectedCaminById", caminService.editCamin(caminId));

        return "pages/layer 4/camine/crud camine/update_info_camin";
    }

    /* ~~~~~~~~~~~ Update Camin and Redirect to Camine Page ~~~~~~~~~~~ */
    @PostMapping(path = "/update/{caminId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String updateCamin(@PathVariable("caminId") Long caminId, Camin newCamin) {
        caminService.updateCamin(caminId, newCamin);

        return "redirect:/admin/camine";
    }

    /* ~~~~~~~~~~~ Clear Fields and Update with 0 and Redirect to Camine Page ~~~~~~~~~~~ */
    @RequestMapping(path = "/clear/{caminId}")
    public String clearCamin(@PathVariable("caminId") Long caminId) {
        //Preluam caminul actual stiind Id-ul
        Camin selectedCamin = caminService.editCamin(caminId);

        selectedCamin.setCapacitate(0);
        selectedCamin.setNrCamereTotal(0);
        selectedCamin.setNrCamereUnStudent(0);
        selectedCamin.setNrCamereDoiStudenti(0);
        selectedCamin.setNrCamereTreiStudenti(0);
        selectedCamin.setNrCamerePatruStudenti(0);

        caminService.clearCamin(selectedCamin.getId(), selectedCamin);

        return "redirect:/admin/camine";
    }
}
