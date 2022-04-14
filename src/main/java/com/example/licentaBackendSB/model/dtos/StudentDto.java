package com.example.licentaBackendSB.model.dtos;

import com.example.licentaBackendSB.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private Long id;
    private String anUniversitar;
    private String nume;
    private String prenume;
    private String grupa;
    private String serie;
    private Integer an;
    private Double medie;
    private String zi_de_nastere;
    private String cnp;
    private String judet;
    @Enumerated(EnumType.STRING)
    private Gender genSexual;
    private String myToken;
    private String friendToken;
    private String camin_preferat;
    private String flagCazSpecial;
}
