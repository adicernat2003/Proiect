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

    List<StudentAplicant> findAllByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar);

    Optional<StudentAplicant> getStudentCaminByCnpAndNumeCamin(String cnp, String numeCamin);

    //Update friendToken knowing CNP
    @Modifying
    @Query("update StudentAplicant set friendToken = ?1 where cnp = ?2 and numeCamin = ?3")
    void updateFriendTokenFromStudentInCamin(String friendToken, String cnp, String numeCamin);

    //Delete student din tabelul de camin care are anumite fielduri identice
    @Transactional
    @Modifying
    void removeStudentCaminByMyTokenAndCnpAndNumeAndPrenumeAndNumeCamin(String studentToken, String studentCNP, String studetNume, String studentPrenume, String numeCamin);
}
