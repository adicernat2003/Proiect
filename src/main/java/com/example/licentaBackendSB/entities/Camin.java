package com.example.licentaBackendSB.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

import static com.example.licentaBackendSB.constants.Constants.ZERO;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder // for building an instance of Camin
public class Camin extends BaseEntity {

    private String numeCamin;
    private Integer capacitate;
    private Integer nrCamereTotal;
    private Integer nrCamereUnStudent;
    private Integer nrCamereDoiStudenti;
    private Integer nrCamereTreiStudenti;
    private Integer nrCamerePatruStudenti;

    public Boolean checkIfValuesAreZero() {
        return ZERO.equals(this.capacitate)
                && ZERO.equals(this.nrCamereTotal)
                && ZERO.equals(this.nrCamereUnStudent)
                && ZERO.equals(this.nrCamereDoiStudenti)
                && ZERO.equals(this.nrCamereTreiStudenti)
                && ZERO.equals(this.nrCamerePatruStudenti);
    }
}
