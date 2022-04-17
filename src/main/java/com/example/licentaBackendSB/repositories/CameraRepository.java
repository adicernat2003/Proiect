package com.example.licentaBackendSB.repositories;

import com.example.licentaBackendSB.model.entities.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional(Transactional.TxType.MANDATORY)
public interface CameraRepository extends JpaRepository<Camera, Integer> {

    List<Camera> findAllByCaminId(Long caminId);

    List<Camera> findAllByCaminIdAndNumarTotalPersoane(Long caminId, Integer numarTotalPersoane);
}
