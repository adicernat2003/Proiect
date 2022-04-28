package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder // for building an instance of CaminP20
public class StudentAplicant extends BaseEntity {

    private String nume;
    private String prenume;
    private String cnp;
    private Double medie;
    private Integer an;
    private String myToken;
    private String friendToken;
    @OneToOne
    private Camin camin;
    private Boolean isCazat;
    @ManyToOne
    private Camera camera;
}
