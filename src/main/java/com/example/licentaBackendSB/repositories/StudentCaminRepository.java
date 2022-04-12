package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.entities.StudentCamin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCaminRepository extends JpaRepository<StudentCamin, Long> {

    @Query("select s from StudentCamin s where s.numeCamin = ?1")
    List<StudentCamin> getAllByNumeCamin(String numeCamin);

    //Update friendToken knowing CNP
    @Transactional
    @Modifying
    @Query("update StudentCamin set friendToken = ?1 where cnp = ?2 and numeCamin = ?3")
    void updateFriendTokenFromStudentInCamin(String friendToken, String cnp, String numeCamin);

    //Get Student knowing CNP
    @Query("select s from StudentCamin s where s.cnp = ?1 and s.numeCamin = ?2")
    Optional<StudentCamin> getStudentFromCamin(String cnp, String numeCamin);

    //Delete student din tabelul de camin care are anumite fielduri identice
    @Transactional
    @Modifying
    @Query("delete from StudentCamin where myToken = ?1 and cnp = ?2 and nume = ?3 and prenume = ?4 and numeCamin = ?5")
    void deleteByNumePrenumeMyTokenCNP(String studentToken, String studentCNP, String studetNume, String studentPrenume, String numeCamin);
}
