package com.example.licentaBackendSB.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.licentaBackendSB.constants.Constants.ZERO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaminDto {
    private Long id;
    private String anUniversitar;
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
