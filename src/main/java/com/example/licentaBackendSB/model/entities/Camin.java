package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static com.example.licentaBackendSB.constants.Constants.ZERO;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true) // for building an instance of Camin
public class Camin extends BaseEntity {

    private String numeCamin;
    private Integer capacitate = 0;
    private Integer nrCamereTotal = 0;
    private Integer nrCamereUnStudent = 0;
    private Integer nrCamereDoiStudenti = 0;
    private Integer nrCamereTreiStudenti = 0;
    private Integer nrCamerePatruStudenti = 0;
    @OneToMany(mappedBy = "camin")
    private List<Camera> camere = new ArrayList<>();

    public Boolean checkIfValuesAreZero() {
        return ZERO.equals(this.capacitate)
                && ZERO.equals(this.nrCamereTotal)
                && ZERO.equals(this.nrCamereUnStudent)
                && ZERO.equals(this.nrCamereDoiStudenti)
                && ZERO.equals(this.nrCamereTreiStudenti)
                && ZERO.equals(this.nrCamerePatruStudenti);
    }
}
