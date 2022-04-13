package com.example.licentaBackendSB.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentAplicantDto {

    private Long id;
    private Integer anUniversitar;
    private String nume;
    private String prenume;
    private String cnp;
    private Double medie;
    private Integer an;
    private String myToken;
    private String friendToken;
    private String numeCamin;
}
