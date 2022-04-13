package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.repositories.CaminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class CamineLoader implements CommandLineRunner {

    private final CaminRepository caminRepository;

    public static List<Camin> camineDB;

    @Override
    public void run(String... args) {
        log.info("Loading data from CamineLoader...");

        camineDB = caminRepository.findAll();
        if (camineDB.isEmpty()) {
            camineDB = hardcodeCamine();
        }

        caminRepository.saveAll(camineDB);
    }

    private static List<Camin> hardcodeCamine() {
        List<Camin> hardcodedListOfCamine = new ArrayList<>();

        hardcodedListOfCamine.add(Camin.builder()
                .numeCamin("Leu A")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .anUniversitar(2021)
                .build());

        hardcodedListOfCamine.add(Camin.builder()
                .numeCamin("Leu C")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .anUniversitar(2021)
                .build());

        hardcodedListOfCamine.add(Camin.builder()
                .numeCamin("P20")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .anUniversitar(2021)
                .build());

        hardcodedListOfCamine.add(Camin.builder()
                .numeCamin("P23")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .anUniversitar(2021)
                .build());

        return hardcodedListOfCamine;
    }
}
