package com.example.licentaBackendSB.model.entities;

import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.enums.Master;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "student")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Student extends BaseEntityForIdsAndAnUniversitar implements Comparable<Student> {

    @Transient
    public static final long startIndexing = 1;
    @Transient
    public static final long endIndexing = 40;

    private String nume;
    private String prenume;
    private String grupa;
    private String serie;
    private String zi_de_nastere;
    private String cnp;
    private String judet;
    private Boolean isCazSpecial = Boolean.FALSE;
    private Boolean alreadySelectedUndesiredCamine = Boolean.FALSE;
    private Boolean alreadySelectedPreferences = Boolean.FALSE;
    private Boolean isMasterand;
    private Integer an;
    private Integer prioritate;
    private Double medie;

    @Enumerated(EnumType.STRING)
    private Master master;

    @Enumerated(EnumType.STRING)
    private Gender genSexual;

    @ManyToOne
    private Camera cameraRepartizata;

    @OneToMany(mappedBy = "student")
    private Map<Camin, Preferinta> preferinte = new TreeMap<>();

    @ManyToMany
    @JoinTable(name = "student_undesired_accommodation", joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "camin_id", referencedColumnName = "id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private final List<Camin> mUndesiredAccommodation = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "student_friends", joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<Student> friends = new ArrayList<>();

    @Override
    public String toString() {
        return this.getFullName() + " " + this.cnp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Student student = (Student) o;
        return getId() != null && Objects.equals(getId(), student.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public int compareTo(Student o) {
        if (!Objects.equals(this.getPrioritate(), o.getPrioritate())) {
            return this.getPrioritate() - o.getPrioritate();
        }
        if (!Objects.equals(this.medie, o.medie)) {
            return -Double.compare(this.getMedie(), o.getMedie());
        }

        return this.getFullName().compareTo(o.getFullName());
    }

    public String getFullName() {
        return this.getNume() + this.getPrenume();
    }
}

