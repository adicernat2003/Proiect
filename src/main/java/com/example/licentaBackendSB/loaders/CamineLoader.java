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
            camineDB = this.hardcodeCamine();
        }

        caminRepository.saveAll(camineDB);
    }

    private List<Camin> hardcodeCamine() {
        List<Camin> hardcodedListOfCamine = new ArrayList<>();

        hardcodedListOfCamine.add(new Camin().toBuilder()
                .numeCamin("Leu A")
                .anUniversitar(2021)
                .build());

        hardcodedListOfCamine.add(new Camin().toBuilder()
                .numeCamin("Leu C")
                .anUniversitar(2021)
                .build());

        hardcodedListOfCamine.add(new Camin().toBuilder()
                .numeCamin("P20")
                .anUniversitar(2021)
                .build());

        hardcodedListOfCamine.add(new Camin().toBuilder()
                .numeCamin("P23")
                .anUniversitar(2021)
                .build());

        return hardcodedListOfCamine;
    }
}
