package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.StudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentAccountRepository extends JpaRepository<StudentAccount, Long> {

    Optional<StudentAccount> findByCnp(String cnp);

}
