package com.example.licentaBackendSB.model.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString
public class StudentAccount extends BaseEntityForIds {

    private String nume;
    private String prenume;
    private String zi_de_nastere;
    private String cnp;
    private String username;
    private String password;
    private String autoritate;
    private Boolean isActive;
}
