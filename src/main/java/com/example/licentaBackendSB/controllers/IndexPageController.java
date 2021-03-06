package com.example.licentaBackendSB.controllers;

import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.services.StudentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexPageController {

    private final StudentAccountService studentAccountService;

    @GetMapping("login")
    public String getLoginView() {
        return "pages/layer 1/login";
    }

    @GetMapping("menu")
    public String getMenuView(Model model) {
        LoggedAccount loggedAccount = new LoggedAccount();
        Boolean isLoggedStandardAcc = loggedAccount.checkIfStandardAccLogged();

        if (isLoggedStandardAcc) {
            model.addAttribute("loggedStudentAccount", loggedAccount.getLoggedUsername());
            model.addAttribute("isLoggedStandardAcc", "true");
            model.addAttribute("authority", loggedAccount.getAuthorityOfStandardAcc());
        } else {
            StudentAccount loggedStudentAccount = studentAccountService.getLoggedStudentAccount();
            model.addAttribute("loggedStudentAccount", loggedStudentAccount);
            model.addAttribute("isLoggedStandardAcc", "false");
            model.addAttribute("authority", loggedStudentAccount.getAutoritate());
        }

        return "pages/layer 2/menu";
    }

}
