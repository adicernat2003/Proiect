package com.example.licentaBackendSB.utils;

import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Preferinta;
import com.example.licentaBackendSB.repositories.CameraRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.licentaBackendSB.constants.Constants.*;

@Component
@AllArgsConstructor
public class StringUtils {

    private final CameraRepository cameraRepository;

    public String concatenateStrings(String a, String b) {
        return a + " " + b;
    }

    public String extractNumeCameraFromString(String numeCamera) {
        String[] numeCameraSiCapacitate = numeCamera.split(",");
        return numeCameraSiCapacitate[0];
    }

    public String mapIntegerNumarPersoaneCameraToString(Integer numarPersoaneCamera) {
        return switch (numarPersoaneCamera) {
            case 1 -> O_PERSOANA;
            case 2 -> DOUA_PERSOANE;
            case 3 -> TREI_PERSOANE;
            case 4 -> PATRU_PERSOANE;
            default -> throw new IllegalArgumentException("Numar incorect de persoane!");
        };
    }

    public List<String> mapCamereToNumarCamere(List<Preferinta> preferinte) {
        return preferinte
                .stream()
                .flatMap(preferinta -> cameraRepository.getAllCamereOfPreferinta(preferinta.getId()).stream())
                .map(Camera::getNumarCamera)
                .toList();
    }
}
