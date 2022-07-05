package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.managers.Manager;
import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.repositories.CameraRepository;
import com.example.licentaBackendSB.repositories.CaminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.licentaBackendSB.constants.Constants.DEFAULT_YEAR;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(4)
public class CamereLoader implements CommandLineRunner {

    public static List<Camera> camereDB;
    private final CameraRepository cameraRepository;
    private final CaminRepository caminRepository;
    private final Manager manager;

    @Override
    public void run(String... args) {
        log.info("Loading data from CamereLoader...");

        camereDB = cameraRepository.findAll();
        if (camereDB.isEmpty()) {
            camereDB = this.hardCodeCamere();
        }

        cameraRepository.saveAll(camereDB);
    }

    private List<Camera> hardCodeCamere() {
        List<Camera> hardcodedListOfCamere = new ArrayList<>();
        List<Camin> camine = caminRepository.findAllByAnUniversitar(DEFAULT_YEAR);
        for (Camin camin : camine) {
            List<Object[]> optiuniCamere = caminRepository.getAllCamereOptiuniByNumeCaminAndAnUniversitar(camin.getNumeCamin(), DEFAULT_YEAR);
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < (Integer) optiuniCamere.get(0)[j]; i++) {
                    hardcodedListOfCamere.add(new Camera().toBuilder()
                            .anUniversitar(camin.getAnUniversitar())
                            .numarTotalPersoane(j + 1)
                            .camin(camin)
                            .numarCamera(manager.getRandomRoomNumber(camin.getNumeCamin()))
                            .build());
                }
            }
        }
        return hardcodedListOfCamere;
    }
}
