package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Camera extends BaseEntity {

    @ManyToOne
    private Camin camin;
    private String numarCamera;
    private Integer numarTotalPersoane;
    private Integer numarCurentPersoane = 0;
    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL)
    private List<StudentCazat> studenti = new ArrayList<>();
}
