package com.example.licentaBackendSB.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "preferinta_camera")
@Getter
@Setter
public class PreferintaCamera extends BaseEntityForIds {
}
