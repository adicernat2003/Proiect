package com.example.licentaBackendSB.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.licentaBackendSB.constants.Constants.*;

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

    public Boolean checkIfValuesAreDefaultValues() {
        return DEFAULT_CAMIN_CAPACITY.equals(this.capacitate)
                && DEFAULT_NUMBER_OF_ROOMS.equals(this.nrCamereTotal)
                && DEFAULT_NUMBER_OF_EACH_KIND_OF_ROOM.equals(this.nrCamereUnStudent)
                && DEFAULT_NUMBER_OF_EACH_KIND_OF_ROOM.equals(this.nrCamereDoiStudenti)
                && DEFAULT_NUMBER_OF_EACH_KIND_OF_ROOM.equals(this.nrCamereTreiStudenti)
                && DEFAULT_NUMBER_OF_EACH_KIND_OF_ROOM.equals(this.nrCamerePatruStudenti);
    }

}
