package com.example.licentaBackendSB.others;

import lombok.Getter;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.example.licentaBackendSB.constants.Constants.*;

@Getter
public class LoggedAccount {

    private final String loggedUsername;

    public LoggedAccount() {
        this.loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public String getLoggedUsername() {
        return loggedUsername;
    }

    public Boolean checkIfStandardAccLogged() {
        return this.loggedUsername.equals("checu")
                || this.loggedUsername.equals("Adrian")
                || this.loggedUsername.equals("lixi");
    }

    public String getAuthorityOfStandardAcc() {
        return switch (this.loggedUsername) {
            case ("checu") -> STUDENT;
            case ("Adrian") -> ADMIN;
            default -> ASSISTANT;
        };
    }
}
