package com.example.licentaBackendSB.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "student_friends")
@Getter
@Setter
public class StudentFriends extends BaseEntityForIds {
}
