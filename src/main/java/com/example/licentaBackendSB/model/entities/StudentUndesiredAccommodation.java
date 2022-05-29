package com.example.licentaBackendSB.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "student_undesired_accommodation")
@Getter
@Setter
public class StudentUndesiredAccommodation extends BaseEntityForIds {
}
