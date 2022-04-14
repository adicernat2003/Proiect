package com.example.licentaBackendSB.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Master {
    AWC("AWC"),
    CMob("CMob"),
    MSR("MSR"),
    TSAC("TSAC"),
    TC("TC"),
    ACES("ACES"),
    AM("AM"),
    MN("MN"),
    MS("MS"),
    NBIM("NBIM"),
    OE("OE"),
    EIA("EIA"),
    EIM("EIM"),
    EPIC("EPIC"),
    ICSFET("ICSFET"),
    IISC("IISC"),
    TAEA("TAEA"),
    PCON("PCON"),
    SIVA("SIVA"),
    TAID("TAID");

    private String numeMaster;
}
