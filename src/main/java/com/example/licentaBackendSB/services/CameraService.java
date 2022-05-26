package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Preferinta;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.repositories.CameraRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CameraService {

    private final CameraRepository cameraRepository;
    private final StudentRepository studentRepository;

    public int getAvailableSpots(Long cameraId) {
        Camera camera = cameraRepository.getById(cameraId);
        return camera.getNumarTotalPersoane() - camera.getMAssignedStudents().size();
    }

    public boolean isEmpty(Long cameraId) {
        Camera camera = cameraRepository.getById(cameraId);
        return camera.getMAssignedStudents().isEmpty() && camera.getMAssignedGender() == null;
    }

    public boolean isFull(Long cameraId) {
        Camera camera = cameraRepository.getById(cameraId);
        return camera.getMAssignedStudents().size() == camera.getNumarTotalPersoane();
    }

    public void assignStudent(Long cameraId, Student student) {
        if (this.isFull(cameraId)) {
            throw new RuntimeException("Unable to assign student to full room ");
        }
        Camera camera = cameraRepository.getById(cameraId);
        if (camera.getMAssignedGender() != null && camera.getMAssignedGender() != student.getGenSexual()) {
            throw new RuntimeException("Unable to assign student " + student + " to room " + camera + ". Wrong gender.");
        }

        camera.getMAssignedStudents().add(student);
        camera.setMAssignedGender(student.getGenSexual());
        student.setCameraRepartizata(camera);

        cameraRepository.save(camera);
        studentRepository.save(student);
    }

    public boolean isPrefferedBy(Long cameraId, Student student) {
        Camera camera = cameraRepository.getById(cameraId);
        return camera.getMPreferedBy().contains(student);
    }

    public void removePreference(Long cameraId, Student student) {
        Camera camera = cameraRepository.getById(cameraId);
        camera.getMPreferedBy().remove(student);
        cameraRepository.save(camera);
        // setare si pe preferinta
    }

    public boolean isIn(Long cameraId, Camin camin) {
        Camera camera = cameraRepository.getById(cameraId);
        return camera.getCamin().equals(camin);
    }

    public void setPrefferedBy(Long cameraId, Student student) {
        Camera camera = cameraRepository.getById(cameraId);
        camera.getMPreferedBy().add(student);
        cameraRepository.save(camera);
        // setare si pe preferinta
    }

    public Optional<Student> getAssignedStudent(Long cameraId, int index) {
        Camera camera = cameraRepository.getById(cameraId);
        return index < camera.getMAssignedStudents().size() ? Optional.of(camera.getMAssignedStudents().get(index)) : Optional.empty();
    }

    public void removeStudent(Long cameraId, Student student) {
        Camera camera = cameraRepository.getById(cameraId);
        camera.getMPreferedBy().remove(student);
        // setare si pe preferinta

        if (camera.getMAssignedStudents().isEmpty()) {
            camera.setMAssignedGender(null);
        }
        cameraRepository.save(camera);
    }

    public List<Camera> getAllCamereOfPreferinta(Preferinta preferinta) {
        return cameraRepository.getAllCamereByPreferinta(preferinta.getId());
    }

    public List<Camera> getAllCamerePreferredByStudent(Long studentId) {
        return cameraRepository.getAllPreferredCamereByStudent(studentId);
    }
}
