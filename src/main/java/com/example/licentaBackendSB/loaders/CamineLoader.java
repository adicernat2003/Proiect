package com.example.licentaBackendSB.loaders;

import com.example.licentaBackendSB.entities.Camin;
import com.example.licentaBackendSB.repositories.CaminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class CamineLoader implements CommandLineRunner {

    private final CaminRepository caminRepository;

    @Override
    public void run(String... args) {
        log.info("Loading data from CamineLoader...");

        List<Camin> camineDB = Camin.hardcodedCamine;

        caminRepository.saveAll(camineDB);
    }
}
