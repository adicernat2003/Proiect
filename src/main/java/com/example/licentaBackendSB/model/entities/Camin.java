package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.licentaBackendSB.constants.Constants.*;

@Entity
@Table
@Getter
@Setter// for getters/setters
@AllArgsConstructor // for constructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true) // for building an instance of Camin
public class Camin extends BaseEntity implements Comparable<Camin> {

    private String numeCamin;
    private Integer capacitate = DEFAULT_CAMIN_CAPACITY;
    private Integer nrCamereTotal = DEFAULT_NUMBER_OF_ROOMS;
    private Integer nrCamereUnStudent = DEFAULT_NUMBER_OF_EACH_KIND_OF_ROOM;
    private Integer nrCamereDoiStudenti = DEFAULT_NUMBER_OF_EACH_KIND_OF_ROOM;
    private Integer nrCamereTreiStudenti = DEFAULT_NUMBER_OF_EACH_KIND_OF_ROOM;
    private Integer nrCamerePatruStudenti = DEFAULT_NUMBER_OF_EACH_KIND_OF_ROOM;

    @OneToMany(mappedBy = "camin")
    private List<Camera> camere = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Camin camin = (Camin) o;
        return getId() != null && Objects.equals(getId(), camin.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }


    @Override
    public int compareTo(Camin o) {
        return numeCamin.compareTo(o.numeCamin);
    }
}
