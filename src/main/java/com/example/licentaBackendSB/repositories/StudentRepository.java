package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select s.id from Student s where s.anUniversitar = ?1")
    List<Long> findAllIdsOfStudendsByAnUniversitar(Integer anUniversitar);

    Integer countAllByAnUniversitar(Integer anUniversitar);

    List<Student> findAllByAnUniversitar(Integer anUniversitar);

    @Query(nativeQuery = true, value = "select * from student s where s.an_universitar = ?1 AND s.camera_repartizata_id IS null")
    List<Student> findAllNecazatiByAnUniversitar(Integer anUniversitar);

    Optional<Student> getStudentByNumeAndPrenumeAndAnUniversitar(String nume, String prenume, Integer anUniversitar);

    @Query("SELECT MIN(s.anUniversitar) FROM Student s WHERE s.nume = ?1 AND s.prenume = ?2")
    Optional<Integer> getLowestAnUniversitarForStudent(String nume, String prenume);

    Integer countAllByCnp(String cnp);

    List<Student> findAllByGenSexualAndAnUniversitar(Gender gender, Integer anUniversitar);

    @Query("select s from Student s where s.cameraRepartizata.camin = ?1 and s.anUniversitar = ?2")
    List<Student> findAllByCaminAndAnUniversitar(Camin camin, Integer anUniversitar);

    @Query(nativeQuery = true, value = "select * from Student s where s.id in (select sf.friend_id from student_friends sf WHERE sf.student_id = ?1)")
    List<Student> findAllFriendsOfStudent(Long studentId);

    @Query(nativeQuery = true, value = "select s.id from Student s where s.id in (select sf.friend_id from student_friends sf WHERE sf.student_id = ?1)")
    List<Long> findAllFriendsIdsOfStudent(Long studentId);

    @Query(nativeQuery = true, value = "select distinct student_id, friend_id from student_friends")
    List<Object[]> getDistinctPairs();

    @Query(nativeQuery = true, value = "select id from student_friends where student_id = ?1 AND friend_id = ?2")
    List<BigInteger> getIdsForPair(BigInteger studentId, BigInteger friendId);

    @Query(nativeQuery = true, value = "update student s set s.camera_repartizata_id = ?1 where s.id = ?2")
    @Modifying
    @Transactional
    void insertCameraRepartizataToStudent(Long cameraId, Long studentId);

    @Query(nativeQuery = true, value = "select * from student s where s.id IN (select scp.student_id from student_camera_preferata scp where scp.camera_id = ?1)")
    List<Student> getAllStudentsThatPreferCamera(Long cameraId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "insert into student_undesired_accomodation values (null, ?1, ?2)")
    void insertRowIntoStudentUndesiredAccomodation(Long studentId, Long caminId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from student_undesired_accomodation sua where sua.student_id  = ?1")
    void deleteAllUndesiredCamineOfStudent(Long studentId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from student_undesired_accomodation sua where sua.student_id  = ?1 and sua.camin_id = ?2")
    void deleteUndesiredCaminOfStudent(Long studentId, Long caminId);

    @Query(nativeQuery = true, value = "update student s set s.already_selected_undesired_camine = ?1 where s.id = ?2")
    @Modifying
    @Transactional
    void setAlreadySelectedUndesiredCamineOfStudent(boolean option, Long studentId);
}
