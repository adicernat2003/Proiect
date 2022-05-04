package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Camin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaminRepository extends JpaRepository<Camin, Long> {

    Integer countAllByAnUniversitar(Integer anUniversitar);

    List<Camin> findAllByAnUniversitar(Integer anUniversitar);

    Optional<Camin> findCaminByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar);

    @Query("select c.nrCamereUnStudent, c.nrCamereDoiStudenti, c.nrCamereTreiStudenti, c.nrCamerePatruStudenti from Camin c where c.numeCamin = ?1 and c.anUniversitar = ?2")
    List<Object[]> getAllCamereOptiuniByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar);

    @Query(nativeQuery = true, value = "select * from camin c where c.id in (select sf.camin_id from student_camine_preferate sf WHERE sf.student_id = ?1)")
    List<Camin> findAllCaminePreferateOfStudent(Long studentId);
}
