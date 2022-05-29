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

@Service
@RequiredArgsConstructor
public class CameraService {

    private final CameraRepository cameraRepository;
    private final StudentRepository studentRepository;

    public int getAvailableSpots(Long cameraId) {
        Camera camera = cameraRepository.getById(cameraId);
        long numberOfStudentsAccommodatedToCamera = cameraRepository.getAllStudentsAccommodatedToCamera(cameraId).size();
        return (int) (camera.getNumarTotalPersoane() - numberOfStudentsAccommodatedToCamera);
    }

    public boolean isEmpty(Long cameraId) {
        Camera camera = cameraRepository.getById(cameraId);
        List<Student> listOfStudentsAccommodatedToCamera = cameraRepository.getAllStudentsAccommodatedToCamera(cameraId);
        return listOfStudentsAccommodatedToCamera.isEmpty() && camera.getMAssignedGender() == null;
    }

    public boolean isFull(Long cameraId) {
        Camera camera = cameraRepository.getById(cameraId);
        long numberOfStudentsAccommodatedToCamera = cameraRepository.getAllStudentsAccommodatedToCamera(cameraId).size();
        System.out.println(camera.getNumarCamera() + " are " + numberOfStudentsAccommodatedToCamera + " studenti cazati momentan, maximul este de " + camera.getNumarTotalPersoane());
        return numberOfStudentsAccommodatedToCamera == camera.getNumarTotalPersoane();
    }

    public void assignStudent(Long cameraId, Student student) {
        if (this.isFull(cameraId)) {
            throw new RuntimeException("Unable to assign student to full room ");
        }
        Camera camera = cameraRepository.getById(cameraId);
        student = studentRepository.getById(student.getId());
        if (camera.getMAssignedGender() != null && camera.getMAssignedGender() != student.getGenSexual()) {
            throw new RuntimeException("Unable to assign student " + student + " to room " + camera + ". Wrong gender.");
        }

        studentRepository.insertCameraRepartizataToStudent(cameraId, student.getId());
        camera.setMAssignedGender(student.getGenSexual());
        cameraRepository.save(camera);
    }

    public void removePreference(Long cameraId, Student student) {
        Camera camera = cameraRepository.getById(cameraId);
        cameraRepository.deleteRowFromStudentCameraPreferata(camera.getId(), student.getId());
        // setare si pe preferinta
    }

    public boolean isIn(Long cameraId, Camin camin) {
        Camera camera = cameraRepository.getById(cameraId);
        return camera.getCamin().equals(camin);
    }

    public Camera setPrefferedBy(Long cameraId, Student student) {
        cameraRepository.insertRowIntoStudentCameraPreferata(cameraId, student.getId());
        return cameraRepository.getById(cameraId);
    }

    public Camera removeStudent(Long cameraId, Student student) {
        cameraRepository.deleteRowFromStudentCameraPreferata(cameraId, student.getId());
        Camera camera = cameraRepository.getById(cameraId);
        if (cameraRepository.getAllStudentsAccommodatedToCamera(cameraId).isEmpty()) {
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
