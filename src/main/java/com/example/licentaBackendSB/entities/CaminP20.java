package com.example.licentaBackendSB.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@Builder // for building an instance of CaminP20
public class CaminP20 {

    @Id
    @SequenceGenerator(
            name = "caminP20_sequence",
            sequenceName = "caminP20_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "caminP20_sequence"
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

    //Others methods
    public static CaminP20 convertStudentToCaminP20(Student student) {
        return CaminP20.builder()
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
