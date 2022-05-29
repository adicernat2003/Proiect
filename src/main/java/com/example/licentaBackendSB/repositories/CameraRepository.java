package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {

    long countAllByCaminId(Long caminId);

    List<Camera> findAllByCaminId(Long caminId);

    List<Camera> findAllByCaminIdAndNumarTotalPersoane(Long caminId, Integer numarTotalPersoane);

    List<Camera> findAllByAnUniversitar(Integer anUniversitar);

    Camera findByNumarCameraAndAnUniversitar(String numarCamera, Integer anUniversitar);

    @Query(nativeQuery = true, value = "select * from camera c where c.id IN (select pc.camera_id from preferinta_camera pc where pc.preferinta_id = ?1)")
    List<Camera> getAllCamereByPreferinta(Long preferintaId);

    @Query(nativeQuery = true, value = "select * from camera c where c.id IN (select scp.camera_id from student_camera_preferata scp where scp.student_id = ?1)")
    List<Camera> getAllPreferredCamereByStudent(Long studentId);

    @Query("select s from Student s where s.cameraRepartizata.id = ?1")
    List<Student> getAllStudentsAccommodatedToCamera(Long cameraId);

    @Query(nativeQuery = true, value = "delete from student_camera_preferata scp where scp.camera_id = ?1 AND scp.student_id = ?2")
    @Modifying
    @Transactional
    void deleteRowFromStudentCameraPreferata(Long cameraId, Long studentId);

    @Query(nativeQuery = true, value = "insert into student_camera_preferata values (null, :cameraId, :studentId)")
    @Modifying
    @Transactional
    void insertRowIntoStudentCameraPreferata(Long cameraId, Long studentId);

    @Query(nativeQuery = true, value = """
            select *
            from camera c
            where c.id IN (select pc.camera_id
                           from preferinta_camera pc
                           where pc.preferinta_id IN (select p.id from preferinta p where p.student_id = ?1));
            """)
    List<Camera> getAllCamerePreferateOfStudent(Long studentId);

    @Query(nativeQuery = true, value = "select * from camera c where c.id IN (select pc.camera_id from preferinta_camera pc where pc.preferinta_id = ?1)")
    List<Camera> getAllCamereOfPreferinta(Long preferintaId);

    @Query(nativeQuery = true, value = "insert into preferinta_camera values (null, :preferintaId, :cameraId)")
    @Modifying
    @Transactional
    void insertIntoPreferintaCamera(Long preferintaId, Long cameraId);
}
