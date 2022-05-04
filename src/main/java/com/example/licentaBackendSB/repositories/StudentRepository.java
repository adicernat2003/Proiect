package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Integer countAllByAnUniversitar(Integer anUniversitar);

    List<Student> findAllByAnUniversitar(Integer anUniversitar);

    Optional<Student> getStudentByNumeAndPrenumeAndAnUniversitar(String nume, String prenume, Integer anUniversitar);

    @Query("SELECT MIN(s.anUniversitar) FROM Student s WHERE s.nume = ?1 AND s.prenume = ?2")
    Optional<Integer> getLowestAnUniversitarForStudent(String nume, String prenume);

    Integer countAllByCnp(String cnp);

    List<Student> findAllByGenSexualAndAnUniversitar(Gender gender, Integer anUniversitar);

    @Query("select s from Student s where s.caminRepartizat = ?1 and s.anUniversitar = ?2")
    List<Student> findAllByCaminAndAnUniversitar(Camin camin, Integer anUniversitar);

    @Query(nativeQuery = true, value = "select * from Student s where s.id in (select sf.friend_id from student_friends sf WHERE sf.student_id = ?1)")
    List<Student> findAllFriendsOfStudent(Long studentId);
}
