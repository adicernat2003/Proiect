package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.StudentAplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(Transactional.TxType.MANDATORY)
public interface StudentCaminRepository extends JpaRepository<StudentAplicant, Long> {

    @Query("SELECT s FROM StudentAplicant s WHERE s.camin.numeCamin = ?1 AND s.anUniversitar = ?2")
    List<StudentAplicant> findAllByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar);

    @Query("SELECT s FROM StudentAplicant s WHERE s.cnp = ?1 AND s.anUniversitar = ?2 AND s.camin.numeCamin = ?3")
    Optional<StudentAplicant> getStudentCaminByCnpAndAnUniversitarAndNumeCamin(String cnp, Integer anUniversitar, String numeCamin);

    //Update friendToken knowing CNP
    @Modifying
    @Query("update StudentAplicant set friendToken = ?1 where cnp = ?2 and anUniversitar = ?3")
    void updateFriendTokenFromStudentInCamin(String friendToken, String cnp, Integer anUniversitar);

    //Delete student din tabelul de camin care are anumite fielduri identice
    @Transactional
    @Modifying
    @Query("DELETE FROM StudentAplicant where cnp = ?1 and anUniversitar = ?2")
    void removeStudentCaminByCnpAndAnUniversitar(String studentCNP, Integer anUniversitar);
}
