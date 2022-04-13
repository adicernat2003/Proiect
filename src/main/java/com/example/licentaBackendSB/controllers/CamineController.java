package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.converters.CaminConverter;
import com.example.licentaBackendSB.model.dtos.CaminDto;
import com.example.licentaBackendSB.model.entities.Camin;
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
    private final CaminConverter caminConverter;

    /* ~~~~~~~~~~~ Get Camine View ~~~~~~~~~~~ */
    @GetMapping("/{anUniversitar}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getCaminePage(@PathVariable String anUniversitar, Model model) {
        model.addAttribute("listOfCamine", caminService.getCamineByAnUniversitar(Integer.parseInt(anUniversitar)));
        model.addAttribute("leuA", caminService.getCaminByNumeCaminAndAnUniversitar(LEU_A, Integer.parseInt(anUniversitar)));
        model.addAttribute("leuC", caminService.getCaminByNumeCaminAndAnUniversitar(LEU_C, Integer.parseInt(anUniversitar)));
        model.addAttribute("p20", caminService.getCaminByNumeCaminAndAnUniversitar(P20, Integer.parseInt(anUniversitar)));
        model.addAttribute("p23", caminService.getCaminByNumeCaminAndAnUniversitar(P23, Integer.parseInt(anUniversitar)));

        return "pages/layer 4/camine/camine_page";
    }

    @GetMapping("/{anUniversitar}/students/{numeCamin}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ASSISTANT')")
    public String getStudents(@PathVariable String anUniversitar,
                              @PathVariable String numeCamin,
                              Model model) {
        model.addAttribute("listOfStudents", studentCaminService.getStudents(numeCamin, Integer.parseInt(anUniversitar)));

        return "pages/layer 4/camine/tables/studentCaminList";
    }

    /* ~~~~~~~~~~~ Get Camin knowing ID ~~~~~~~~~~~ */
    @GetMapping(path = "/edit/{caminId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String editCamin(@PathVariable("caminId") Long caminId, Model model) {
        Camin camin = caminService.getCaminById(caminId);
        CaminDto caminDto = caminConverter.convertCaminEntityToDto(camin);
        model.addAttribute("selectedCaminById", caminDto);

        return "pages/layer 4/camine/crud camine/update_info_camin";
    }

    /* ~~~~~~~~~~~ Update Camin and Redirect to Camine Page ~~~~~~~~~~~ */
    @PostMapping(path = "/update/{caminId}")
    @PreAuthorize("hasAuthority('student:write')")
    public String updateCamin(@PathVariable("caminId") Long caminId, CaminDto newCamin) {
        caminService.updateCamin(caminId, newCamin);

        return "redirect:/admin/camine/" + caminService.getCaminById(caminId).getAnUniversitar();
    }

    /* ~~~~~~~~~~~ Clear Fields and Update with 0 and Redirect to Camine Page ~~~~~~~~~~~ */
    @GetMapping(path = "/clear/{caminId}")
    public String clearCamin(@PathVariable("caminId") Long caminId) {
        //Preluam caminul actual stiind Id-ul
        Camin selectedCamin = caminService.getCaminById(caminId);

        selectedCamin.setCapacitate(0);
        selectedCamin.setNrCamereTotal(0);
        selectedCamin.setNrCamereUnStudent(0);
        selectedCamin.setNrCamereDoiStudenti(0);
        selectedCamin.setNrCamereTreiStudenti(0);
        selectedCamin.setNrCamerePatruStudenti(0);

        caminService.clearCamin(selectedCamin.getId(), selectedCamin);

        return "redirect:/admin/camine/" + selectedCamin.getAnUniversitar();
    }
}
