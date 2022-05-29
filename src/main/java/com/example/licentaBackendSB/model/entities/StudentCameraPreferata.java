package com.example.licentaBackendSB.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "student_camera_preferata")
@Getter
@Setter
public class StudentCameraPreferata extends BaseEntityForIds {
}
