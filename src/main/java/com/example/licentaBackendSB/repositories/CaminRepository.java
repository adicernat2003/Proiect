package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Camin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaminRepository extends JpaRepository<Camin, Long> {

    List<Camin> findAllByAnUniversitar(Integer anUniversitar);

    Optional<Camin> findCaminByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar);

    @Query("select c.nrCamereUnStudent, c.nrCamereDoiStudenti, c.nrCamereTreiStudenti, c.nrCamerePatruStudenti from Camin c where c.numeCamin = ?1 and c.anUniversitar = ?2")
    List<Object[]> getAllCamereOptiuniByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar);

    @Query(nativeQuery = true, value = "select * from camin c where c.id IN (select sua.camin_id from student_undesired_accommodation sua where sua.student_id = ?1)")
    List<Camin> getAllUndesiredCamineOfStudent(Long studentId);

    @Query(nativeQuery = true, value = "select * from camin c where c.id in (select p.preferinte_key from preferinta p where p.student_id = ?1)")
    List<Camin> getAllCamineOfStudentPreferences(Long studentId);

    @Query(nativeQuery = true, value = "select * from camin c where c.id = (select p.preferinte_key from preferinta p where p.id = ?1)")
    Camin getCaminOfPreferinta(Long preferintaId);

    @Query(nativeQuery = true, value = "select c.camin_id from camera c where c.id = ?1")
    Long getCaminIdOfCamera(Long cameraId);

    @Query(nativeQuery = true, value = "select * from camin c where c.id = (select c.camin_id from camera c where c.id = ?1)")
    Camin getCaminOfCamera(Long cameraId);
}
