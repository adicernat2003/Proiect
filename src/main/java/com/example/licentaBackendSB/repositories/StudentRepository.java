package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(Transactional.TxType.MANDATORY)
public interface StudentRepository extends JpaRepository<Student, Long> {
    Integer countAllByAnUniversitar(Integer anUniversitar);

    List<Student> findAllByAnUniversitar(Integer anUniversitar);

    Optional<Student> getStudentByNumeAndPrenumeAndAnUniversitar(String nume, String prenume, Integer anUniversitar);

    @Query("SELECT MIN(s.anUniversitar) FROM Student s WHERE s.nume = ?1 AND s.prenume = ?2")
    Optional<Integer> getLowestAnUniversitarForStudent(String nume, String prenume);

    //get student knowing mytoken
    Optional<Student> getStudentByMyToken(String myToken);

    //checks if exists friend token in db
    @Query("select case when (count(s.myToken) > 0) then true else false end from Student s  where s.myToken = ?1")
    Boolean validateFriendTokenExists(String friendToken);

    Integer countAllByCnp(String cnp);
}
