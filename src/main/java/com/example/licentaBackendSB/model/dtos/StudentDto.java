package com.example.licentaBackendSB.model.dtos;

import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.enums.Master;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

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
    private Boolean isCazSpecial;
    @Enumerated
    private Master master;
    private Boolean isMasterand;
    private List<String> numarLocuriCamera;
    private List<String> friends;
    private List<String> caminePreferate;
    private List<String> camerePreferate;

    @Override
    public String toString() {
        return nume + " " + prenume;
    }
}
