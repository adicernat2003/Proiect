package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Preferinta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreferintaRepository extends JpaRepository<Preferinta, Long> {

    @Query("select p from Preferinta p where p.student.id = ?1")
    List<Preferinta> findAllPreferencesOfStudent(Long studentId);
}
