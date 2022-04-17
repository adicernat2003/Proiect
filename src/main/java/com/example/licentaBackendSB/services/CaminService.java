package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.converters.CaminConverter;
import com.example.licentaBackendSB.managers.Manager;
import com.example.licentaBackendSB.model.dtos.CaminDto;
import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.repositories.CameraRepository;
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
    private final CameraRepository cameraRepository;
    private final Manager manager;

    //Methods
    /* Get all Camine */
    public List<CaminDto> getCamineByAnUniversitar(Integer anUniversitar) {
        return caminRepository.findAllByAnUniversitar(anUniversitar)
                .stream()
                .map(caminConverter::mapCaminEntityToDto)
                .toList();
    }

    public CaminDto getCaminByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar) {
        Optional<Camin> caminOptional = caminRepository.findCaminByNumeCaminAndAnUniversitar(numeCamin, anUniversitar);
        if (caminOptional.isPresent()) {
            return caminConverter.mapCaminEntityToDto(caminOptional.get());
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
    public Integer updateCamin(Long caminId, CaminDto newCamin) {
        Optional<Camin> caminOptional = caminRepository.findById(caminId);
        if (caminOptional.isPresent()) {
            Camin foundCamin = caminOptional.get();
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
                if (newCamin.getNrCamereUnStudent() > foundCamin.getNrCamereUnStudent()) {
                    this.addNewCamere(foundCamin, newCamin.getNrCamereUnStudent() - foundCamin.getNrCamereUnStudent(), 1);
                } else {
                    this.deleteCamere(foundCamin, foundCamin.getNrCamereUnStudent() - newCamin.getNrCamereUnStudent(), 1);
                }
                foundCamin.setNrCamereUnStudent(newCamin.getNrCamereUnStudent());
            }

            /** update nr camere cu doi student*/
            if (newCamin.getNrCamereDoiStudenti() > 0
                    && !foundCamin.getNrCamereDoiStudenti().equals(newCamin.getNrCamereDoiStudenti())) {
                if (newCamin.getNrCamereDoiStudenti() > foundCamin.getNrCamereDoiStudenti()) {
                    this.addNewCamere(foundCamin, newCamin.getNrCamereDoiStudenti() - foundCamin.getNrCamereDoiStudenti(), 2);
                } else {
                    this.deleteCamere(foundCamin, foundCamin.getNrCamereDoiStudenti() - newCamin.getNrCamereDoiStudenti(), 2);
                }
                foundCamin.setNrCamereDoiStudenti(newCamin.getNrCamereDoiStudenti());
            }

            /** update nr camere cu trei student*/
            if (newCamin.getNrCamereTreiStudenti() > 0
                    && !foundCamin.getNrCamereTreiStudenti().equals(newCamin.getNrCamereTreiStudenti())) {
                if (newCamin.getNrCamereTreiStudenti() > foundCamin.getNrCamereTreiStudenti()) {
                    this.addNewCamere(foundCamin, newCamin.getNrCamereTreiStudenti() - foundCamin.getNrCamereTreiStudenti(), 3);
                } else {
                    this.deleteCamere(foundCamin, foundCamin.getNrCamereTreiStudenti() - newCamin.getNrCamereTreiStudenti(), 3);
                }
                foundCamin.setNrCamereTreiStudenti(newCamin.getNrCamereTreiStudenti());
            }

            /** update nr camere cu patru student*/
            if (newCamin.getNrCamerePatruStudenti() > 0
                    && !foundCamin.getNrCamerePatruStudenti().equals(newCamin.getNrCamerePatruStudenti())) {
                if (newCamin.getNrCamerePatruStudenti() > foundCamin.getNrCamerePatruStudenti()) {
                    this.addNewCamere(foundCamin, newCamin.getNrCamerePatruStudenti() - foundCamin.getNrCamerePatruStudenti(), 4);
                } else {
                    this.deleteCamere(foundCamin, foundCamin.getNrCamerePatruStudenti() - newCamin.getNrCamerePatruStudenti(), 4);
                }
                foundCamin.setNrCamerePatruStudenti(newCamin.getNrCamerePatruStudenti());
            }
            return caminRepository.save(foundCamin).getAnUniversitar();
        } else {
            throw new IllegalStateException("Camin with id " + caminId + " does not exist");
        }
    }

    /* Clear Camin Fields and update them with 0 */
    public Integer clearCamin(Long caminId) {
        Optional<Camin> caminOptional = caminRepository.findById(caminId);
        if (caminOptional.isPresent()) {
            List<Camera> camere = cameraRepository.findAllByCaminId(caminId);
            Camin camin = caminOptional.get();
            camin.setCapacitate(0);
            camin.setNrCamereTotal(0);
            camin.setNrCamereUnStudent(0);
            camin.setNrCamereDoiStudenti(0);
            camin.setNrCamereTreiStudenti(0);
            camin.setNrCamerePatruStudenti(0);
            cameraRepository.deleteAll(camere);
            return camin.getAnUniversitar();
        } else {
            throw new IllegalStateException("Camin with id " + caminId + " does not exist");
        }
    }

    private void deleteCamere(Camin foundCamin, Integer numberOfCamereToBeRemoved, Integer numarTotalPersoane) {
        List<Camera> camerePentruUnStudent = cameraRepository.findAllByCaminIdAndNumarTotalPersoane(foundCamin.getId(), numarTotalPersoane)
                .stream().limit(numberOfCamereToBeRemoved).toList();
        cameraRepository.deleteAll(camerePentruUnStudent);
    }

    private void addNewCamere(Camin foundCamin, Integer numberOfCamereToBeAdded, Integer numarTotalPersoane) {
        for (int i = 0; i < numberOfCamereToBeAdded; i++) {
            Camera cameraPentruUnStudent = this.buildCameraForUpdatedValuesOfCamin(numarTotalPersoane, foundCamin);
            foundCamin.getCamere().add(cameraPentruUnStudent);
            cameraRepository.save(cameraPentruUnStudent);
        }
    }

    private Camera buildCameraForUpdatedValuesOfCamin(Integer numarTotalPersoane, Camin camin) {
        return new Camera().toBuilder()
                .numarCamera(manager.getRandomRoomNumber(camin.getNumeCamin()))
                .camin(camin)
                .anUniversitar(camin.getAnUniversitar())
                .numarTotalPersoane(numarTotalPersoane)
                .build();
    }
}
