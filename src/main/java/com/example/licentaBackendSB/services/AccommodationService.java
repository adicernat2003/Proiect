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
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final StudentRepository studentRepository;
    private final CaminRepository caminRepository;
    private final CameraService cameraService;
    private final StudentService studentService;
    private final PreferintaRepository preferintaRepository;
    private final CaminService caminService;
    private final CameraRepository cameraRepository;

    public void accommodateStudents(Integer anUniversitar) {
        List<Student> allStudents = studentRepository.findAllByAnUniversitar(anUniversitar);
        List<Camin> allBuildings = caminRepository.findAllByAnUniversitar(anUniversitar);
        Map<String, Student> mOriginalStudents = new TreeMap<>();
        Set<Student> mWaitingList = new TreeSet<>();
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

        //implementarea mea
        while (!mStudentsToPlace.isEmpty()) {
            Student student = mStudentsToPlace.pollFirst();
            student = studentRepository.getById(student.getId());
            System.out.println("Accommodating " + student.getFullName() + "...");
            Set<Camin> buildingsToTry = new TreeSet<>(mBuildings.values());
            buildingsToTry.removeAll(studentService.getHatedList(student.getId()));
            System.out.printf("Accommodating %s (%s), buildings to try: %s\n", student.getFullName(), student.getCnp(), buildingsToTry);
            System.out.print("==================================================================\n");
            boolean accommodated = false;
            List<Preferinta> preferinte = preferintaRepository.findAllPreferencesOfStudent(student.getId());
            for (Preferinta preferinta : preferinte) {
                Camin camin = caminService.getCaminOfPreferinta(preferinta);
                System.out.printf("\tHas preference: %s\n", camin.getNumeCamin());
                if (!buildingsToTry.contains(camin)) {
                    System.out.print("\t\tNot accepted!\n");
                    continue;
                }
                if (!caminService.hasSpotForGender(camin.getId(), student.getGenSexual())) {
                    buildingsToTry.remove(camin);
                    System.out.print("\t\tNo room!\n");
                    continue;
                }

                Optional<Student> reserveForColleague = Optional.empty();
                List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
                for (Student colleague : friends) {
                    System.out.printf("\t\tWants to live with %s (%s)... ", colleague.getFullName(), colleague.getCnp());
                    if (colleague.getCameraRepartizata() != null && cameraService.isIn(colleague.getCameraRepartizata().getId(), camin)
                            && !cameraService.isFull(colleague.getCameraRepartizata().getId())) {
                        studentService.setAccommodation(student.getId(), colleague.getCameraRepartizata().getId());
                        System.out.printf("Solution found! Room: %s\n", student.getCameraRepartizata());
                        accommodated = true;
                        break;
                    } else if (colleague.getCameraRepartizata() == null) {
                        System.out.printf("No solution: not accomodated (reserving for colleague %s).\n", colleague);
                        reserveForColleague = Optional.of(colleague);
                        break;
                    } else if (!cameraService.isIn(colleague.getCameraRepartizata().getId(), camin)) {
                        System.out.print("No solution: not accomodated in this buildind.\n");
                    } else if (cameraService.isFull(colleague.getCameraRepartizata().getId())) {
                        System.out.print("No solution: room full.\n");
                    } else {
                        System.out.print("No solution: unknown reason.\n");
                    }
                }
                if (accommodated) {
                    break;
                }
                List<Camera> camere = cameraService.getAllCamereOfPreferinta(preferinta);
                for (Camera camera : camere) {
                    System.out.printf("\t\tWants room %s... ", camera);
                    if (!cameraService.isFull(camera.getId()) && (camera.getMAssignedGender() != null ? camera.getMAssignedGender() : student.getGenSexual()).equals(student.getGenSexual())) {
                        studentService.setAccommodation(student.getId(), camera.getId());
                        System.out.printf("Solution found! Room: %s\n", camera);
                        accommodated = true;
                        break;
                    } else {
                        System.out.print("No solution!\n");
                    }
                }
                if (accommodated) {
                    break;
                }
                if (reserveForColleague == null) {
                    System.out.print("\t\tLooking for partially occupied non-desired room... ");
                    Student finalStudent = student;
                    Optional<Camera> partiallyOccupiedRoom = caminService.getCaminOfPreferinta(preferinta).getCamere()
                            .stream()
                            .filter(camera -> !cameraService.isEmpty(camera.getId()) && !cameraService.isFull(camera.getId())
                                    && camera.getMAssignedGender().equals(finalStudent.getGenSexual()) && camera.getMPreferedBy().isEmpty())
                            .findAny();
                    if (partiallyOccupiedRoom.isPresent()) {
                        System.out.printf("Solution found! Room: %s\n", partiallyOccupiedRoom);
                        studentService.setAccommodation(student.getId(), partiallyOccupiedRoom.get().getId());
                        accommodated = true;
                        break;
                    } else {
                        System.out.print("No solution.\n");
                    }
                    System.out.print("\t\tLooking for empty non-desired room... ");
                    Optional<Camera> emptyRoom = caminService.getCaminOfPreferinta(preferinta).getCamere()
                            .stream()
                            .filter(camera -> cameraService.isEmpty(camera.getId()) && camera.getMPreferedBy().isEmpty())
                            .findAny();
                    if (emptyRoom.isPresent()) {
                        studentService.setAccommodation(student.getId(), emptyRoom.get().getId());
                        System.out.printf("Solution found! Room: %s\n", emptyRoom);
                        accommodated = true;
                        if (reserveForColleague.isPresent()) {
                            System.out.printf("\t\t\tAdding reservation for %s\n", reserveForColleague);
                            studentService.addPriorityAccommodationPreference(emptyRoom.get().getCamin().getId(), emptyRoom.get().getId(), student.getId());
                        }
                        break;
                    } else {
                        System.out.print("No solution.\n");
                    }
                } else {
                    System.out.print("\t\tLooking for empty non-desired room... ");
                    Optional<Camera> emptyRoom = caminService.getCaminOfPreferinta(preferinta).getCamere()
                            .stream().
                            filter(camera -> cameraService.isEmpty(camera.getId()) && camera.getMPreferedBy().isEmpty())
                            .findAny();
                    if (emptyRoom.isPresent()) {
                        studentService.setAccommodation(student.getId(), emptyRoom.get().getId());
                        System.out.printf("Solution found! Room: %s\n", emptyRoom);
                        accommodated = true;
                        if (reserveForColleague.isPresent()) {
                            System.out.printf("\t\t\tAdding reservation for %s\n", reserveForColleague);
                            studentService.addPriorityAccommodationPreference(emptyRoom.get().getCamin().getId(), emptyRoom.get().getId(), student.getId());
                        }
                        break;
                    } else {
                        System.out.print("No solution.\n");
                    }

                    System.out.print("\t\tLooking for partially occupied non-desired room... ");
                    Student finalStudent1 = student;
                    Optional<Camera> partiallyOccupiedRoom = caminService.getCaminOfPreferinta(preferinta).getCamere()
                            .stream()
                            .filter(camera -> !cameraService.isEmpty(camera.getId()) && !cameraService.isFull(camera.getId())
                                    && camera.getMAssignedGender().equals(finalStudent1.getGenSexual()) && camera.getMPreferedBy().isEmpty())
                            .findAny();
                    if (partiallyOccupiedRoom.isPresent()) {
                        System.out.printf("Solution found! Room: %s\n", partiallyOccupiedRoom);
                        studentService.setAccommodation(student.getId(), partiallyOccupiedRoom.get().getId());
                        accommodated = true;
                        break;
                    } else {
                        System.out.print("No solution.\n");
                    }
                }

                System.out.print("\t\tLooking for a spot in a least desired room... ");
                Student finalStudent2 = student;
                Optional<Camera> lastChanceRoom = caminService.getCaminOfPreferinta(preferinta).getCamere()
                        .stream()
                        .filter(camera -> (cameraService.isEmpty(camera.getId()) || (!cameraService.isFull(camera.getId())
                                && camera.getMAssignedGender().equals(finalStudent2.getGenSexual())))
                                && finalStudent2.compareTo(new TreeSet<>(camera.getMPreferedBy()).last()) < 0)
                        .max(Comparator.comparing(camera -> new TreeSet<>(camera.getMPreferedBy()).last()));
                if (lastChanceRoom.isPresent()) {
                    studentService.setAccommodation(student.getId(), lastChanceRoom.get().getId());
                    accommodated = true;
                    System.out.printf("Solution found! Room: %s\n", lastChanceRoom);
                    break;
                } else {
                    System.out.print("No solution.\n");
                }
                buildingsToTry.remove(caminService.getCaminOfPreferinta(preferinta));
            }

            if (!accommodated) {
                if (!buildingsToTry.isEmpty()) {
                    List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
                    for (Student colleague : friends) {
                        System.out.printf("\t\tWants to live with %s (%s)... ", colleague.getFullName(), colleague.getCnp());
                        if (colleague.getCameraRepartizata() != null && !cameraService.isFull(colleague.getCameraRepartizata().getId())) {
                            studentService.setAccommodation(student.getId(), colleague.getCameraRepartizata().getId());
                            System.out.printf("Solution found! Room: %s\n", student.getCameraRepartizata());
                            break;
                        } else {
                            System.out.printf("No solution.\n", student.getCameraRepartizata());
                        }
                    }
                    System.out.print("\tThere are some options left, move to redistribution list!\n");
                    mGlobalRedistributionList.add(student);
                } else {
                    System.out.print("\tNo room left, request rejected.\n");
                    Student finalStudent3 = student;
                    Optional<Student> nextInLine = mWaitingList.stream()
                            .filter(waitingStudent -> studentService.getCycleString(waitingStudent.getId()).equals(studentService.getCycleString(finalStudent3.getId())))
                            .findFirst();
                    nextInLine.ifPresent(mStudentsToPlace::add);
                }
            }
        }

        while (!mGlobalRedistributionList.isEmpty()) {
            Student student = mGlobalRedistributionList.pollFirst();
            student = studentRepository.getById(student.getId());
            System.out.printf("Redistributing %s (%s),...\n", student.getFullName(), student.getCnp());
            System.out.print("==================================================================\n");
            boolean accommodated = false;
            List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
            for (Student colleague : friends) {
                System.out.printf("\tWants to live with %s (%s)... ", colleague.getFullName(), colleague.getCnp());
                if (colleague.getCameraRepartizata() != null && !cameraService.isFull(colleague.getCameraRepartizata().getId())
                        && !studentService.getHatedList(student.getId()).contains(colleague.getCameraRepartizata().getCamin())) {
                    System.out.printf("\t\tSolution found! Room: %s\n", colleague.getCameraRepartizata().getNumarCamera());
                    studentService.setAccommodation(student.getId(), colleague.getCameraRepartizata().getId());
                    accommodated = true;
                    break;
                } else {
                    System.out.printf("\t\tNo solution.\n", student.getCameraRepartizata());
                }
            }

            if (!accommodated) {
                for (Camin camin : mBuildings.values()) {
                    if (studentService.getHatedList(student.getId()).contains(camin)) {
                        continue;
                    }
                    System.out.printf("\tLooking for empty room in %s... ", camin);
                    List<Camera> camereOfCamin = cameraRepository.findAllByCaminId(camin.getId());

                    Optional<Camera> emptyRoom = camereOfCamin.stream()
                            .filter(camera -> cameraService.isEmpty(camera.getId()) && camera.getMPreferedBy().isEmpty())
                            .findAny();

                    if (emptyRoom.isPresent()) {
                        studentService.setAccommodation(student.getId(), emptyRoom.get().getId());
                        System.out.printf("\t\tSolution found! Room: %s\n", emptyRoom.get().getNumarCamera());
                        accommodated = true;
                        break;
                    } else {
                        System.out.printf("\t\tNo solution.\n");
                    }
                }
            }

            if (!accommodated) {
                for (Camin camin : mBuildings.values()) {
                    System.out.printf("\tTrying %s... ", camin);
                    if (caminService.hasSpotForGender(camin.getId(), student.getGenSexual()) && !studentService.getHatedList(student.getId()).contains(camin)) {
                        Camera camera = caminService.getSpotForGender(camin.getId(), student.getGenSexual());
                        studentService.setAccommodation(student.getId(), camera.getId());
                        System.out.printf("Solution found! Room: %s\n", camera.getNumarCamera());
                        accommodated = true;
                        break;
                    } else {
                        System.out.printf("No solution.\n");
                    }
                }
            }
            if (!accommodated) {
                System.out.printf("\tNo room left, request rejected.\n");
                Student finalStudent = student;
                Optional<Student> nextInLine = mWaitingList.stream()
                        .filter(s -> studentService.getCycleString(s.getId()).equals(studentService.getCycleString(finalStudent.getId())))
                        .findFirst();
                if (nextInLine.isPresent()) {
                    mGlobalRedistributionList.add(nextInLine.get());
                    mWaitingList.remove(nextInLine.get());
                }
            }
        }

        for (Camin camin : mBuildings.values()) {
            System.out.printf("Building %s: %d free spots, %d girls, %d boys, %d neutral\n",
                    camin.toString(), caminService.getAllFreeSpots(camin.getId()), caminService.getFreeSpots(camin.getId(),
                            Gender.FEMININ), caminService.getFreeSpots(camin.getId(), Gender.MASCULIN), caminService.getFreeSpots(camin.getId()));
        }

    }
}
