package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Camin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(Transactional.TxType.MANDATORY)
public interface CaminRepository extends JpaRepository<Camin, Long> {

    List<Camin> findAllByAnUniversitar(Integer anUniversitar);

    Optional<Camin> findCaminByNumeCaminAndAnUniversitar(String numeCamin, Integer anUniversitar);
}
