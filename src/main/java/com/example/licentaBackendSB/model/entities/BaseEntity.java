package com.example.licentaBackendSB.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor // for constructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer anUniversitar = 2021;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return anUniversitar != null ? anUniversitar.equals(that.anUniversitar) : that.anUniversitar == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (anUniversitar != null ? anUniversitar.hashCode() : 0);
        return result;
    }
}
