package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder // for building an instance of CaminP20
public class StudentCazat extends BaseEntity {

    private String nume;
    private String prenume;
    private String cnp;
    private Double medie;
    private Integer an;
    @ManyToOne
    private Camera camera;
}
