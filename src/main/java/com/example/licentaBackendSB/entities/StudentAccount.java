package com.example.licentaBackendSB.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder // for building an instance of StudentAccount
@ToString
public class StudentAccount extends BaseEntity {

    private String nume;
    private String prenume;
    private String zi_de_nastere;
    private String cnp;
    private String username;
    private String password;
    private String autoritate;
}
