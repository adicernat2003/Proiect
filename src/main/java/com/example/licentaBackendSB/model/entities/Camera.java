package com.example.licentaBackendSB.model.entities;

import com.example.licentaBackendSB.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "camera")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Camera extends BaseEntityForIdsAndAnUniversitar implements Comparable<Camera> {

    private String numarCamera;
    private Integer numarTotalPersoane;

    @Enumerated(EnumType.STRING)
    private Gender mAssignedGender;

    @ManyToOne
    private Camin camin;

    @OneToMany(mappedBy = "cameraRepartizata")
    private List<Student> mAssignedStudents = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "student_camera_preferata", joinColumns = @JoinColumn(name = "camera_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<Student> mPreferredBy = new TreeSet<>();

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

    @Override
    public int compareTo(Camera camera) {
        if (this.getCamin().equals(camera.getCamin())) {
            return this.getCamin().compareTo(camera.getCamin());
        }
        return this.numarCamera.compareTo(camera.numarCamera);
    }

}
