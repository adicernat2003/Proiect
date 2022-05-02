package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "camera")
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Camera extends BaseEntity {
    private String numarCamera;
    private Integer numarTotalPersoane;
    private Integer numarCurentPersoane = 0;

    @ManyToOne
    private Camin camin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Camera camera = (Camera) o;
        return getId() != null && Objects.equals(getId(), camera.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
