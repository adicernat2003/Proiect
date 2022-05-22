package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.model.entities.Preferinta;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.repositories.CameraRepository;
import com.example.licentaBackendSB.repositories.CaminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferintaService {

    private final CameraRepository cameraRepository;
    private final CaminRepository caminRepository;

    public List<Preferinta> findAllPreferencesOfStudent(Student student) {
        return student.getPreferinte().values()
                .stream()
                .toList();
    }

}
