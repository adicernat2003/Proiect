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
@Table(name = "camera")
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Camera extends BaseEntity {
    private String numarCamera;
    private Integer numarTotalPersoane;
    private Integer numarCurentPersoane = 0;
    @ManyToOne
    private Camin camin;
    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL)
    private List<StudentAplicant> studenti = new ArrayList<>();
}
