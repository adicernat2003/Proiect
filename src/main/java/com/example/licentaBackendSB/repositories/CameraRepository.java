package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {

    List<Camera> findAllByCaminId(Long caminId);

    List<Camera> findAllByCaminIdAndNumarTotalPersoane(Long caminId, Integer numarTotalPersoane);

    List<Camera> findAllByAnUniversitar(Integer anUniversitar);

    Camera findByNumarCameraAndAnUniversitar(String numarCamera, Integer anUniversitar);

    @Query(nativeQuery = true, value = "select * from camera c where c.id IN (select pc.camera_id from preferinta_camera pc where pc.preferinta_id = ?1)")
    List<Camera> getAllCamereByPreferinta(Long preferintaId);

    @Query(nativeQuery = true, value = "select * from camera c where c.id IN (select scp.camera_id from student_camera_preferata scp where scp.student_id = ?1)")
    List<Camera> getAllPreferredCamereByStudent(Long studentId);
}
