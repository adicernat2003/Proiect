package com.example.licentaBackendSB.others;

import lombok.Getter;
import org.springframework.security.core.context.SecurityContextHolder;

//Aceasta este dedicata preluarii si prelucarii informatiilor din sesiunea de logare actuala
@Getter
public class LoggedAccount {

    //Fields
    private final String loggedUsername;

    //Constructor
    public LoggedAccount() {
        this.loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    }

    //Getter and Setters
    public String getLoggedUsername() {
        return loggedUsername;
    }

    //Methods
    public Boolean checkIfStandardAccLogged() {
        return this.loggedUsername.equals("checu")
                || this.loggedUsername.equals("iancu")
                || this.loggedUsername.equals("lixi");
    }

    public String getAuthorityOfStandardAcc() {
        return switch (this.loggedUsername) {
            case ("checu") -> "STUDENT";
            case ("iancu") -> "ADMIN";
            default -> "ASSISTANT";
        };
    }
}
