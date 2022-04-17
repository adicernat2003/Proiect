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

    public Camin mapCaminDtoToEntity(CaminDto caminDto) {
        return Camin.builder()
                .anUniversitar(Integer.parseInt(caminDto.getAnUniversitar()))
                .numeCamin(caminDto.getNumeCamin())
                .id(caminDto.getId())
                .capacitate(caminDto.getCapacitate())
                .nrCamereDoiStudenti(caminDto.getNrCamereDoiStudenti())
                .nrCamerePatruStudenti(caminDto.getNrCamerePatruStudenti())
                .nrCamereTotal(caminDto.getNrCamereTotal())
                .nrCamereTreiStudenti(caminDto.getNrCamereTreiStudenti())
                .nrCamereUnStudent(caminDto.getNrCamereUnStudent())
                .numeCamin(caminDto.getNumeCamin())
                .capacitate(caminDto.getCapacitate())
                .build();
    }

}
