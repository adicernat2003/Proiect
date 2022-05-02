package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity //for hibernate framework
@Table(name = "student_camine_preferate")  //for database
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
public class StudentCaminePreferate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
