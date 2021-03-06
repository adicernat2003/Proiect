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

    @Query(nativeQuery = true, value = "delete from preferinta p where p.id = ?1")
    @Modifying
    @Transactional
    void deleteRowFromPreferinta(Long preferintaId);

    @Query(nativeQuery = true, value = "delete from preferinta_camera pc where pc.preferinta_id = ?1 AND pc.camera_id = ?2")
    @Modifying
    @Transactional
    void deleteRowFromPreferintaCamera(Long preferintaId, Long cameraId);

    @Query(nativeQuery = true, value = "update preferinta p set p.preferinte_key = ?1 where p.id = ?2")
    @Modifying
    @Transactional
    void updateCaminOfPreferinta(Long caminId, Long preferintaId);
}
