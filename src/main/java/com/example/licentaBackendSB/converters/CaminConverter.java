package com.example.licentaBackendSB.converters;

import com.example.licentaBackendSB.model.dtos.CaminDto;
import com.example.licentaBackendSB.model.entities.Camin;
import org.springframework.stereotype.Component;

@Component
public class CaminConverter {

    public CaminDto mapCaminEntityToDto(Camin camin) {
        return CaminDto.builder()
                .anUniversitar(String.valueOf(camin.getAnUniversitar()))
                .numeCamin(camin.getNumeCamin())
                .id(camin.getId())
                .capacitate(camin.getCapacitate())
                .nrCamereDoiStudenti(camin.getNrCamereDoiStudenti())
                .nrCamerePatruStudenti(camin.getNrCamerePatruStudenti())
                .nrCamereTotal(camin.getNrCamereTotal())
                .nrCamereTreiStudenti(camin.getNrCamereTreiStudenti())
                .nrCamereUnStudent(camin.getNrCamereUnStudent())
                .numeCamin(camin.getNumeCamin())
                .capacitate(camin.getCapacitate())
                .build();
    }
}
