package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Preferinta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PreferintaRepository extends JpaRepository<Preferinta, Long> {

    @Query("select p from Preferinta p where p.student.id = ?1 ORDER BY p.id")
    List<Preferinta> findAllPreferencesOfStudent(Long studentId);

    @Query(nativeQuery = true, value = "delete from preferinta_camera pc where pc.preferinta_id = ?1 AND pc.camera_id = ?2")
    @Modifying
    @Transactional
    void deleteRowFromPreferintaCamera(Long preferintaId, Long cameraId);
}
