package com.example.licentaBackendSB.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@Builder // for building an instance of Camin
public class Camin {

    @Transient
    public static List<Camin> hardcodedCamine = hardcodeCamine();

    @Id
    @SequenceGenerator(
            name = "camin_sequence",
            sequenceName = "camin_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "camin_sequence"
    )

    //Fields
    private Long id;
    private String numeCamin;
    private Integer capacitate;
    private Integer nrCamereTotal;
    private Integer nrCamereUnStudent;
    private Integer nrCamereDoiStudenti;
    private Integer nrCamereTreiStudenti;
    private Integer nrCamerePatruStudenti;

    //Methods
    private static List<Camin> hardcodeCamine() {
        List<Camin> hardcodedListOfCamine = new ArrayList<>();

        hardcodedListOfCamine.add(Camin.builder()
                .id(1L)
                .numeCamin("Leu A")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .build());

        hardcodedListOfCamine.add(Camin.builder()
                .id(2L)
                .numeCamin("Leu C")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .build());

        hardcodedListOfCamine.add(Camin.builder()
                .id(3L)
                .numeCamin("P20")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .build());

        hardcodedListOfCamine.add(Camin.builder()
                .id(4L)
                .numeCamin("P23")
                .capacitate(0)
                .nrCamereTotal(0)
                .nrCamereUnStudent(0)
                .nrCamereDoiStudenti(0)
                .nrCamereTreiStudenti(0)
                .nrCamerePatruStudenti(0)
                .build());

        return hardcodedListOfCamine;
    }

    public Boolean checkIfValuesAreZero() {
        return (this.capacitate.equals(0))
                && (this.nrCamereTotal.equals(0))
                && (this.nrCamereUnStudent.equals(0))
                && (this.nrCamereDoiStudenti.equals(0))
                && (this.nrCamereTreiStudenti.equals(0))
                && (this.nrCamerePatruStudenti.equals(0));
    }
}
