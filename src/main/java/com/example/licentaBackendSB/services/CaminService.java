package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.converters.CaminConverter;
import com.example.licentaBackendSB.model.dtos.CaminDto;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.repositories.CaminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class CaminService {
    //Fields
    private final CaminRepository caminRepository;
    private final CaminConverter caminConverter;

    //Methods
    /* Get all Camine */
    public List<CaminDto> getCamineByAnUniversitar(Integer anUniversitar) {
        return caminRepository.findAllByAnUniversitar(anUniversitar)
                .stream()
                .map(caminConverter::convertCaminEntityToDto)
                .toList();
    }

    public CaminDto getCaminByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar) {
        Optional<Camin> caminOptional = caminRepository.findCaminByNumeCaminAndAnUniversitar(numeCamin, anUniversitar);
        if (caminOptional.isPresent()) {
            return caminConverter.convertCaminEntityToDto(caminOptional.get());
        } else {
            throw new IllegalArgumentException("Invalid camin numeCamin: " + numeCamin + " and anUniversitar: " + anUniversitar);
        }
    }

    /* Get Id of Camin to update Fields */
    public Camin getCaminById(Long caminId) {
        Optional<Camin> caminOptional = caminRepository.findById(caminId);
        if (caminOptional.isPresent()) {
            return caminOptional.get();
        } else {
            throw new IllegalArgumentException("Invalid camin Id:" + caminId);
        }
    }

    /* Update Camin Fields */
    public void updateCamin(Long caminId, CaminDto newCamin) {
        caminRepository.findById(caminId)
                .map(foundCamin -> {
                    //Validari si Verificari

                    /** update capacitate*/
                    if (newCamin.getCapacitate() > 0
                            && !foundCamin.getCapacitate().equals(newCamin.getCapacitate())) {
                        foundCamin.setCapacitate(newCamin.getCapacitate());
                    }

                    /** update nr camere total*/
                    if (newCamin.getNrCamereTotal() > 0
                            && !foundCamin.getNrCamereTotal().equals(newCamin.getNrCamereTotal())) {
                        foundCamin.setNrCamereTotal(newCamin.getNrCamereTotal());
                    }

                    /** update nr camere cu un student*/
                    if (newCamin.getNrCamereUnStudent() > 0
                            && !foundCamin.getNrCamereUnStudent().equals(newCamin.getNrCamereUnStudent())) {
                        foundCamin.setNrCamereUnStudent(newCamin.getNrCamereUnStudent());
                    }

                    /** update nr camere cu doi student*/
                    if (newCamin.getNrCamereDoiStudenti() > 0
                            && !foundCamin.getNrCamereDoiStudenti().equals(newCamin.getNrCamereDoiStudenti())) {
                        foundCamin.setNrCamereDoiStudenti(newCamin.getNrCamereDoiStudenti());
                    }

                    /** update nr camere cu trei student*/
                    if (newCamin.getNrCamereTreiStudenti() > 0
                            && !foundCamin.getNrCamereTreiStudenti().equals(newCamin.getNrCamereTreiStudenti())) {
                        foundCamin.setNrCamereTreiStudenti(newCamin.getNrCamereTreiStudenti());
                    }

                    /** update nr camere cu patru student*/
                    if (newCamin.getNrCamerePatruStudenti() > 0
                            && !foundCamin.getNrCamerePatruStudenti().equals(newCamin.getNrCamerePatruStudenti())) {
                        foundCamin.setNrCamerePatruStudenti(newCamin.getNrCamerePatruStudenti());
                    }

                    return caminRepository.save(foundCamin);
                })
                .orElseThrow(() -> new IllegalStateException("Camin with id " + caminId + " does not exist"));
    }

    /* Clear Camin Fields and update them with 0 */
    public void clearCamin(Long caminId, Camin newCamin) {
        caminRepository.findById(caminId)
                .map(foundCamin -> {
                    //Validari si Verificari

                    /** update capacitate*/
                    if (!foundCamin.getCapacitate().equals(newCamin.getCapacitate())) {
                        foundCamin.setCapacitate(newCamin.getCapacitate());
                    }

                    /** update nr camere total*/
                    if (!foundCamin.getNrCamereTotal().equals(newCamin.getNrCamereTotal())) {
                        foundCamin.setNrCamereTotal(newCamin.getNrCamereTotal());
                    }

                    /** update nr camere cu un student*/
                    if (!foundCamin.getNrCamereUnStudent().equals(newCamin.getNrCamereUnStudent())) {
                        foundCamin.setNrCamereUnStudent(newCamin.getNrCamereUnStudent());
                    }

                    /** update nr camere cu doi student*/
                    if (!foundCamin.getNrCamereDoiStudenti().equals(newCamin.getNrCamereDoiStudenti())) {
                        foundCamin.setNrCamereDoiStudenti(newCamin.getNrCamereDoiStudenti());
                    }

                    /** update nr camere cu trei student*/
                    if (!foundCamin.getNrCamereTreiStudenti().equals(newCamin.getNrCamereTreiStudenti())) {
                        foundCamin.setNrCamereTreiStudenti(newCamin.getNrCamereTreiStudenti());
                    }

                    /** update nr camere cu patru student*/
                    if (!foundCamin.getNrCamerePatruStudenti().equals(newCamin.getNrCamerePatruStudenti())) {
                        foundCamin.setNrCamerePatruStudenti(newCamin.getNrCamerePatruStudenti());
                    }

                    return caminRepository.save(foundCamin);
                })
                .orElseThrow(() -> new IllegalStateException("Camin with id " + caminId + " does not exist"));
    }
}
