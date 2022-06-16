package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.enums.Gender;
import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Preferinta;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.repositories.CameraRepository;
import com.example.licentaBackendSB.repositories.CaminRepository;
import com.example.licentaBackendSB.repositories.PreferintaRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationService {

    private final StudentRepository studentRepository;
    private final CaminRepository caminRepository;
    private final CameraService cameraService;
    private final StudentService studentService;
    private final PreferintaRepository preferintaRepository;
    private final CaminService caminService;
    private final CameraRepository cameraRepository;

    public void accommodateStudents(Integer anUniversitar) {
        List<Student> allStudents = studentRepository.findAllNecazatiByAnUniversitar(anUniversitar);
        List<Camin> allBuildings = caminRepository.findAllByAnUniversitar(anUniversitar);
        Map<String, Student> mOriginalStudents = new TreeMap<>();
        Map<String, Camin> mBuildings = new TreeMap<>();
        TreeSet<Student> mStudentsToPlace;
        TreeSet<Student> mGlobalRedistributionList = new TreeSet<>();

        for (Student student : allStudents) {
            mOriginalStudents.put(student.getCnp(), student);
        }
        for (Camin camin : allBuildings) {
            mBuildings.put(camin.getNumeCamin(), camin);
        }

        mStudentsToPlace = new TreeSet<>(mOriginalStudents.values());

        if (mStudentsToPlace.size() == 0) {
            return;
        }

        while (!mStudentsToPlace.isEmpty()) {
            Student student = mStudentsToPlace.pollFirst();
            student = studentRepository.getById(Objects.requireNonNull(student).getId());
            log.info("Accommodating " + student.getFullName() + "...");
            List<Camin> camine = caminRepository.findAllByAnUniversitar(anUniversitar);
            Set<Camin> buildingsToTry = new TreeSet<>(camine);
            caminRepository.getAllUndesiredCamineOfStudent(student.getId()).forEach(buildingsToTry::remove);
            log.info("Accommodating {} ({}), buildings to try: {}", student.getFullName(), student.getCnp(), buildingsToTry);
            log.info("==================================================================\n");
            boolean accommodated = false;
            List<Preferinta> preferinte = preferintaRepository.findAllPreferencesOfStudent(student.getId());
            for (Preferinta preferinta : preferinte) {
                preferinta = preferintaRepository.getById(preferinta.getId());
                Camin camin = caminService.getCaminOfPreferinta(preferinta);

                if (camin == null) {
                    continue;
                }

                log.info("\tHas preference: {}\n", camin.getNumeCamin());
                if (!buildingsToTry.contains(camin)) {
                    log.info("\t\tNot accepted!\n");
                    continue;
                }
                if (!caminService.hasSpotForGender(camin.getId(), student.getGenSexual())) {
                    buildingsToTry.remove(camin);
                    log.info("\t\tNo room!\n");
                    continue;
                }

                Optional<Student> reserveForColleague = Optional.empty();
                List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
                for (Student colleague : friends) {
                    colleague = studentRepository.getById(colleague.getId());
                    log.info("\t\tWants to live with {} ({})... ", colleague.getFullName(), colleague.getCnp());
                    if (colleague.getCameraRepartizata() != null && cameraService.isIn(colleague.getCameraRepartizata().getId(), camin)
                            && !cameraService.isFull(colleague.getCameraRepartizata().getId())) {
                        studentService.setAccommodation(student.getId(), colleague.getCameraRepartizata().getId());
                        log.info("Solution found! Room: {}\n", student.getCameraRepartizata());
                        accommodated = true;
                        break;
                    } else if (colleague.getCameraRepartizata() == null) {
                        log.info("No solution: not accomodated (reserving for colleague {}).\n", colleague);
                        reserveForColleague = Optional.of(colleague);
                        break;
                    } else if (!cameraService.isIn(colleague.getCameraRepartizata().getId(), camin)) {
                        log.info("No solution: not accomodated in this buildind.\n");
                    } else if (cameraService.isFull(colleague.getCameraRepartizata().getId())) {
                        log.info("No solution: room full.\n");
                    } else {
                        log.info("No solution: unknown reason.\n");
                    }
                }
                if (accommodated) {
                    break;
                }
                preferinta = preferintaRepository.getById(preferinta.getId());
                List<Camera> camere = cameraService.getAllCamereOfPreferinta(preferinta);
                for (Camera camera : camere) {
                    camera = cameraRepository.getById(camera.getId());
                    log.info("\t\tWants room {}... ", camera.getNumarCamera());
                    if (!cameraService.isFull(camera.getId()) && (camera.getMAssignedGender() != null ?
                            camera.getMAssignedGender() : student.getGenSexual()).getGen().equals(student.getGenSexual().getGen())) {
                        studentService.setAccommodation(student.getId(), camera.getId());
                        log.info("Solution found! Room: {}\n", camera.getNumarCamera());
                        accommodated = true;
                        break;
                    } else {
                        log.info("No solution!\n");
                    }
                }
                if (accommodated) {
                    break;
                }
                if (reserveForColleague.isEmpty()) {
                    log.info("\t\tLooking for partially occupied non-desired room... ");
                    Student finalStudent = student;
                    Optional<Camera> partiallyOccupiedRoom =
                            cameraRepository.findAllByCaminId(caminService.getCaminOfPreferinta(preferinta).getId())
                                    .stream()
                                    .filter(camera -> !cameraService.isEmpty(camera.getId()) && !cameraService.isFull(camera.getId())
                                            && camera.getMAssignedGender().equals(finalStudent.getGenSexual())
                                            && studentRepository.getAllStudentsThatPreferCamera(camera.getId()).isEmpty())
                                    .findAny();
                    if (partiallyOccupiedRoom.isPresent()) {
                        log.info("Solution found! Room: {}\n", partiallyOccupiedRoom.get().getNumarCamera());
                        studentService.setAccommodation(student.getId(), partiallyOccupiedRoom.get().getId());
                        accommodated = true;
                        break;
                    } else {
                        log.info("No solution.\n");
                    }
                    log.info("\t\tLooking for empty non-desired room... ");
                    Optional<Camera> emptyRoom =
                            cameraRepository.findAllByCaminId(caminService.getCaminOfPreferinta(preferinta).getId())
                                    .stream()
                                    .filter(camera -> cameraService.isEmpty(camera.getId())
                                            && studentRepository.getAllStudentsThatPreferCamera(camera.getId()).isEmpty())
                                    .findAny();
                    if (emptyRoom.isPresent()) {
                        studentService.setAccommodation(student.getId(), emptyRoom.get().getId());
                        log.info("Solution found! Room: {}\n", emptyRoom.get().getNumarCamera());
                        accommodated = true;

                        break;
                    } else {
                        log.info("No solution.\n");
                    }
                } else {
                    log.info("\t\tLooking for empty non-desired room... ");
                    preferinta = preferintaRepository.getById(preferinta.getId());
                    Optional<Camera> emptyRoom =
                            cameraRepository.findAllByCaminId(caminService.getCaminOfPreferinta(preferinta).getId())
                                    .stream().
                                    filter(camera -> cameraService.isEmpty(camera.getId())
                                            && studentRepository.getAllStudentsThatPreferCamera(camera.getId()).isEmpty())
                                    .findAny();
                    if (emptyRoom.isPresent()) {
                        studentService.setAccommodation(student.getId(), emptyRoom.get().getId());
                        log.info("Solution found! Room: {}\n", emptyRoom.get().getNumarCamera());
                        accommodated = true;

                        log.info("\t\t\tAdding reservation for {}\n", reserveForColleague);
                        studentService.addPriorityAccommodationPreference(emptyRoom.get().getCamin().getId(),
                                emptyRoom.get().getId(), reserveForColleague.get().getId());

                        break;
                    } else {
                        log.info("No solution.\n");
                    }

                    log.info("\t\tLooking for partially occupied non-desired room... ");
                    Student finalStudent1 = student;
                    preferinta = preferintaRepository.getById(preferinta.getId());
                    Optional<Camera> partiallyOccupiedRoom =
                            cameraRepository.findAllByCaminId(caminService.getCaminOfPreferinta(preferinta).getId())
                                    .stream()
                                    .filter(camera -> !cameraService.isEmpty(camera.getId()) && !cameraService.isFull(camera.getId())
                                            && camera.getMAssignedGender().equals(finalStudent1.getGenSexual())
                                            && studentRepository.getAllStudentsThatPreferCamera(camera.getId()).isEmpty())
                                    .findAny();
                    if (partiallyOccupiedRoom.isPresent()) {
                        log.info("Solution found! Room: {}\n", partiallyOccupiedRoom.get().getNumarCamera());
                        studentService.setAccommodation(student.getId(), partiallyOccupiedRoom.get().getId());
                        accommodated = true;
                        break;
                    } else {
                        log.info("No solution.\n");
                    }
                }

                log.info("\t\tLooking for a spot in a least desired room... ");
                Student finalStudent2 = student;
                preferinta = preferintaRepository.getById(preferinta.getId());
                Optional<Camera> lastChanceRoom = cameraRepository.findAllByCaminId(caminService.getCaminOfPreferinta(preferinta).getId())
                        .stream()
                        .filter(camera -> (cameraService.isEmpty(camera.getId())
                                || (!cameraService.isFull(camera.getId()) && camera.getMAssignedGender().equals(finalStudent2.getGenSexual())))
                                && finalStudent2.compareTo(new TreeSet<>(studentRepository.getAllStudentsThatPreferCamera(camera.getId())).last()) < 0)
                        .max(Comparator.comparing(camera -> new TreeSet<>(studentRepository.getAllStudentsThatPreferCamera(camera.getId())).last()));
                if (lastChanceRoom.isPresent()) {
                    studentService.setAccommodation(student.getId(), lastChanceRoom.get().getId());
                    accommodated = true;
                    log.info("Solution found! Room: {}\n", lastChanceRoom.get().getNumarCamera());
                    break;
                } else {
                    log.info("No solution.\n");
                }
                preferinta = preferintaRepository.getById(preferinta.getId());
                buildingsToTry.remove(caminService.getCaminOfPreferinta(preferinta));
            }

            if (!accommodated) {
                if (!buildingsToTry.isEmpty()) {
                    List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
                    for (Student colleague : friends) {
                        log.info("\t\tWants to live with {} ({})... ", colleague.getFullName(), colleague.getCnp());
                        if (colleague.getCameraRepartizata() != null && !cameraService.isFull(colleague.getCameraRepartizata().getId())) {
                            studentService.setAccommodation(student.getId(), colleague.getCameraRepartizata().getId());
                            log.info("Solution found! Room: {}\n", student.getCameraRepartizata());
                            break;
                        } else {
                            log.info("No solution.\n");
                        }
                    }
                    log.info("\tThere are some options left, move to redistribution list!\n");
                    mGlobalRedistributionList.add(student);
                } else {
                    log.info("\tNo room left, request rejected.\n");
                }
            }
        }

        while (!mGlobalRedistributionList.isEmpty()) {
            Student student = mGlobalRedistributionList.pollFirst();
            student = studentRepository.getById(Objects.requireNonNull(student).getId());
            log.info("Redistributing {} ({}),...", student.getFullName(), student.getCnp());
            log.info("==================================================================\n");
            boolean accommodated = false;
            List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
            for (Student colleague : friends) {
                log.info("\tWants to live with {} ({})... ", colleague.getFullName(), colleague.getCnp());
                if (colleague.getCameraRepartizata() != null && !cameraService.isFull(colleague.getCameraRepartizata().getId())
                        && !caminRepository.getAllUndesiredCamineOfStudent(student.getId()).contains(colleague.getCameraRepartizata().getCamin())) {
                    log.info("\t\tSolution found! Room: {}\n", colleague.getCameraRepartizata().getNumarCamera());
                    studentService.setAccommodation(student.getId(), colleague.getCameraRepartizata().getId());
                    accommodated = true;
                    break;
                } else {
                    log.info("\t\tNo solution.\n");
                }
            }

            if (!accommodated) {
                for (Camin camin : mBuildings.values()) {
                    camin = caminRepository.getById(camin.getId());
                    if (caminRepository.getAllUndesiredCamineOfStudent(student.getId()).contains(camin)) {
                        continue;
                    }
                    log.info("\tLooking for empty room in {}... ", camin);
                    List<Camera> camereOfCamin = cameraRepository.findAllByCaminId(camin.getId());

                    Optional<Camera> emptyRoom = camereOfCamin.stream()
                            .filter(camera -> cameraService.isEmpty(camera.getId())
                                    && studentRepository.getAllStudentsThatPreferCamera(camera.getId()).isEmpty())
                            .findAny();

                    if (emptyRoom.isPresent()) {
                        studentService.setAccommodation(student.getId(), emptyRoom.get().getId());
                        log.info("\t\tSolution found! Room: {}\n", emptyRoom.get().getNumarCamera());
                        accommodated = true;
                        break;
                    } else {
                        log.info("\t\tNo solution.\n");
                    }
                }
            }

            if (!accommodated) {
                for (Camin camin : mBuildings.values()) {
                    camin = caminRepository.getById(camin.getId());
                    log.info("Trying {}... ", camin);
                    if (caminService.hasSpotForGender(camin.getId(), student.getGenSexual())
                            && !caminRepository.getAllUndesiredCamineOfStudent(student.getId()).contains(camin)) {
                        Camera camera = caminService.getSpotForGender(camin.getId(), student.getGenSexual());
                        studentService.setAccommodation(student.getId(), camera.getId());
                        log.info("Solution found! Room: {}\n", camera.getNumarCamera());
                        accommodated = true;
                        break;
                    } else {
                        log.info("No solution.\n");
                    }
                }
            }
            if (!accommodated) {
                log.info("\tNo room left, request rejected.");
            }
        }

        for (Camin camin : caminRepository.findAllByAnUniversitar(anUniversitar)) {
            log.info("Building {}: {} free spots, {} girls, {} boys, {} neutral",
                    camin.toString(), caminService.getAllFreeSpots(camin.getId()), caminService.getFreeSpots(camin.getId(),
                            Gender.FEMININ), caminService.getFreeSpots(camin.getId(), Gender.MASCULIN), caminService.getFreeSpots(camin.getId()));
        }

    }
}
