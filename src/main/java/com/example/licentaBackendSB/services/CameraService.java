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
        camera.setMAssignedStudents(camera.getMAssignedStudents().stream().distinct().toList());
        camera = cameraRepository.save(camera);
        return camera.getMAssignedStudents().isEmpty() && camera.getMAssignedGender() == null;
    }

    public boolean isFull(Long cameraId) {
        Camera camera = cameraRepository.getById(cameraId);
        List<Student> students = studentRepository.findAllByAnUniversitar(2021);
        Camera finalCamera = camera;
        long count = students.stream()
                .filter(student -> student.getCameraRepartizata() != null && student.getCameraRepartizata().equals(finalCamera))
                .count();
        camera.setMAssignedStudents(camera.getMAssignedStudents().stream().distinct().toList());
        camera = cameraRepository.save(camera);
        System.out.println(camera.getNumarCamera() + " are " + camera.getMAssignedStudents().size() + " studenti cazati momentan, maximul este de " + camera.getNumarTotalPersoane());
        return camera.getMAssignedStudents().size() == camera.getNumarTotalPersoane() || camera.getNumarTotalPersoane().equals(count);
    }

    public Student assignStudent(Long cameraId, Student student) {
        if (this.isFull(cameraId)) {
            throw new RuntimeException("Unable to assign student to full room ");
        }
        Camera camera = cameraRepository.getById(cameraId);
        student = studentRepository.getById(student.getId());
        if (camera.getMAssignedGender() != null && camera.getMAssignedGender() != student.getGenSexual()) {
            throw new RuntimeException("Unable to assign student " + student + " to room " + camera + ". Wrong gender.");
        }

        camera.getMAssignedStudents().add(student);
        camera.setMAssignedGender(student.getGenSexual());
        camera = cameraRepository.save(camera);
        student.setCameraRepartizata(camera);

        return studentRepository.save(student);
    }

    public boolean isPrefferedBy(Long cameraId, Student student) {
        Camera camera = cameraRepository.getById(cameraId);
        return camera.getMPreferedBy().contains(student);
    }

    public void removePreference(Long cameraId, Student student) {
        student = studentRepository.getById(student.getId());
        Camera camera = cameraRepository.getById(cameraId);
        camera.getMPreferedBy().remove(student);
        cameraRepository.save(camera);
        // setare si pe preferinta
    }

    public boolean isIn(Long cameraId, Camin camin) {
        Camera camera = cameraRepository.getById(cameraId);
        return camera.getCamin().equals(camin);
    }

    public Camera setPrefferedBy(Long cameraId, Student student) {
        Camera camera = cameraRepository.getById(cameraId);
        camera.getMPreferedBy().add(student);
        return cameraRepository.save(camera);
        // setare si pe preferinta
    }

    public Optional<Student> getAssignedStudent(Long cameraId, int index) {
        Camera camera = cameraRepository.getById(cameraId);
        return index < camera.getMAssignedStudents().size() ? Optional.of(camera.getMAssignedStudents().get(index)) : Optional.empty();
    }

    public Camera removeStudent(Long cameraId, Student student) {
        Camera camera = cameraRepository.getById(cameraId);
        camera.getMPreferedBy().remove(student);
        // setare si pe preferinta

        if (camera.getMAssignedStudents().isEmpty()) {
            camera.setMAssignedGender(null);
        }
        return cameraRepository.save(camera);
    }

    public List<Camera> getAllCamereOfPreferinta(Preferinta preferinta) {
        return cameraRepository.getAllCamereByPreferinta(preferinta.getId());
    }

    public List<Camera> getAllCamerePreferredByStudent(Long studentId) {
        return cameraRepository.getAllPreferredCamereByStudent(studentId);
    }
}
