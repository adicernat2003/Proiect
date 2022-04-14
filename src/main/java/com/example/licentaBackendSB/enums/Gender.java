package com.example.licentaBackendSB.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MASCULIN("Masculin"),
    FEMININ("Feminin");

    private String gen;
}
