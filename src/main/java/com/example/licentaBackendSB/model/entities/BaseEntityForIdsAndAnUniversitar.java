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
import java.util.Objects;

import static com.example.licentaBackendSB.constants.Constants.DEFAULT_YEAR;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntityForIdsAndAnUniversitar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer anUniversitar = DEFAULT_YEAR;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntityForIdsAndAnUniversitar that)) return false;
        if (!Objects.equals(id, that.id)) return false;
        return Objects.equals(anUniversitar, that.anUniversitar);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (anUniversitar != null ? anUniversitar.hashCode() : 0);
        return result;
    }
}
