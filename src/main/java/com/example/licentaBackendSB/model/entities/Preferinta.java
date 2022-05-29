package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preferinta")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Preferinta extends BaseEntityForIdsAndAnUniversitar {

    @ManyToOne
    private Student student;

    @ManyToMany
    @JoinTable(name = "preferinta_camera", joinColumns = @JoinColumn(name = "preferinta_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "camera_id", referencedColumnName = "id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Camera> camere = new ArrayList<>();

}
