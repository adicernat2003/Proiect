package com.example.licentaBackendSB.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@Builder // for building an instance of CaminLeuA
public class CaminLeuA {

    @Id
    @SequenceGenerator(
            name = "caminLeuA_sequence",
            sequenceName = "caminLeuA_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "caminLeuA_sequence"
    )

    //Fields
    private Long id;
    private String nume;
    private String prenume;
    private String cnp;
    private Double medie;
    private Integer an;
    private String myToken;
    private String friendToken;

    //Methods
    public static CaminLeuA convertStudentToCaminLeuA(final Student student) {
        return CaminLeuA.builder()
                .id(student.getId())
                .nume(student.getNume())
                .prenume(student.getPrenume())
                .cnp(student.getCnp())
                .medie(student.getMedie())
                .an(student.getAn())
                .friendToken(student.getFriendToken())
                .myToken(student.getMyToken())
                .build();
    }
}
