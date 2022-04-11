package com.example.licentaBackendSB.entities;

import com.example.licentaBackendSB.others.randomizers.DoBandCNPandGenderRandomizer;
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
@Builder // for building an instance of StudentAccount
@ToString
public class StudentAccount {

    @Transient
    public static final List<StudentAccount> studentAccountsList = hardcodeStudentsAccountsDB(Student.hardcodedStudentsList);

    @Id
    @SequenceGenerator(
            name = "studentAcc_sequence",
            sequenceName = "studentAcc_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "studentAcc_sequence"
    )
    //needed for Statistics && Main sources

    //Fields -----------------------------------------------------------------------------------------------------------
    private Long id;
    private String nume;
    private String prenume;
    private String zi_de_nastere;
    private String cnp;
    private String username;
    private String password;
    private String autoritate;

    //Methods ----------------------------------------------------------------------------------------------------------
    public static List<StudentAccount> hardcodeStudentsAccountsDB(List<Student> tmp) {
        List<StudentAccount> studentAccounts = new ArrayList<>();

        for (long i = Student.startIndexing - 1L; i < Student.endIndexing; i++) {
            String username = tmp.get((int) i).getCnp();
            String password = DoBandCNPandGenderRandomizer.splitDoBbyDot(tmp.get((int) i).getZi_de_nastere());

            studentAccounts.add(StudentAccount.builder()
                    .id(i + 1)
                    .nume(tmp.get((int) i).getNume())
                    .prenume(tmp.get((int) i).getPrenume())
                    .zi_de_nastere(tmp.get((int) i).getZi_de_nastere())
                    .username(username)
                    .password(password)
                    .cnp(tmp.get((int) i).getCnp())
                    .autoritate("STUDENT")
                    .build());
        }
        return studentAccounts;
    }
}
