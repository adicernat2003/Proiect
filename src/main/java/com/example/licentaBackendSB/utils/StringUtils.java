package com.example.licentaBackendSB.utils;

import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Preferinta;
import com.example.licentaBackendSB.repositories.CameraRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.example.licentaBackendSB.constants.Constants.*;
import static java.util.Objects.nonNull;

@Component
@AllArgsConstructor
public class StringUtils {

    private final CameraRepository cameraRepository;

    public static String shuffleString(String string) {
        List<String> letters = Arrays.asList(string.split(""));
        Collections.shuffle(letters);
        StringBuilder shuffled = new StringBuilder();
        for (String letter : letters) {
            shuffled.append(letter);
        }
        return shuffled.toString();
    }

    public String concatenateStrings(String a, String b) {
        return a + " " + b;
    }

    public List<String> mapCamineToNumeCamie(Set<Camin> camine) {
        return camine.stream()
                .map(Camin::getNumeCamin)
                .toList();
    }

    public Integer mapStringNumarPersoaneCameraToInteger(String numarPersoaneCamera) {
        return switch (numarPersoaneCamera) {
            case O_PERSOANA -> 1;
            case DOUA_PERSOANE -> 2;
            case TREI_PERSOANE -> 3;
            case PATRU_PERSOANE -> 4;
            default -> 0;
        };
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

    public List<Integer> mapListOfStringsNumarPersoaneCameraToListOfIntegers(List<String> strings) {
        return strings.stream()
                .map(this::mapStringNumarPersoaneCameraToInteger)
                .toList();
    }

    public List<String> mapListOfIntegersNumarPersoaneCameraToListOfString(List<Integer> integers) {
        return nonNull(integers) ? integers.stream()
                .map(this::mapIntegerNumarPersoaneCameraToString)
                .toList() : List.of();
    }

    public List<String> mapCamereToNumarCamere(List<Preferinta> preferinte) {
        return preferinte
                .stream()
                .flatMap(preferinta -> cameraRepository.getAllCamereOfPreferinta(preferinta.getId()).stream())
                .map(Camera::getNumarCamera)
                .toList();
    }
}
