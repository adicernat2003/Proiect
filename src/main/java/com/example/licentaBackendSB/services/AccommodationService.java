package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.repositories.CaminRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final StudentRepository studentRepository;
    private final CaminRepository caminRepository;

    public void accommodateStudents(Integer anUniversitar) {
        List<Student> allStudents = studentRepository.findAllByAnUniversitar(anUniversitar);
        List<Camin> allBuildings = caminRepository.findAllByAnUniversitar(anUniversitar);
        Map<String, Student> mOriginalStudents = new TreeMap<>();
        Set<Student> mWaitingList = new TreeSet<>(allStudents);
        Map<String, Camin> mBuildings = new TreeMap<>();
        TreeSet<Student> mStudentsToPlace;
        TreeSet<Student> mGlobalRedistributionList = new TreeSet<>();

        for (Student student : allStudents) {
            mOriginalStudents.put(student.getCnp(), student);
        }
        for (Camin camin : allBuildings) {
            mBuildings.put(camin.toString(), camin);
        }

        /*
        for (Student student : mOriginalStudents.values()) {
            System.out.println("Validating student " + student.getFullName() + "...");

            for (String stringPreference : student.getPreferinte().values().stream().map()) {
                if (stringPreference.isBlank()) {
                    continue;
                }
                stringPreference = stringPreference.replaceAll("\\s+", "").toUpperCase();
                String[] preference = stringPreference.trim().split("-", 2);
                switch (preference.length) {
                    case 2:
                        if (mBuildings.containsKey(preference[0])) {
                            Building building = mBuildings.get(preference[0]);
                            if (!preference[1].isBlank() && !preference[1].equals("-neprecizata-")) {
                                try {
                                    int roomNumber = Integer.parseInt(preference[1]);
                                    Optional<Room> room = building.getRoomByNumber(roomNumber);
                                    if (room.isPresent()) {
                                        student.addAccommodationPreference(room.get());
                                    }
                                    student.addAccommodationPreference(building);
                                } catch (NumberFormatException e) {
                                    System.out.printf("Room %s does not exist in building %s\n", preference[1], preference[0]);
                                }
                            } else {
                                student.addAccommodationPreference(building);
                            }
                        } else {
                            System.out.printf("Unknown building string: %s for student %s\n", preference[0], student.getFullName());
                        }
                        break;
                    case 1:
                        if (mBuildings.containsKey(preference[0])) {
                            student.addAccommodationPreference(mBuildings.get(preference[0]));
                        } else {
                            System.out.printf("Unknown building string: %s for student %s\n", preference[0], student.getFullName());
                        }
                        break;
                    default:
                        System.out.printf("Unknown preference string: %s\n", stringPreference.trim());
                        break;
                }
            }

            for (String personId : student.getPreferedColleaguesIds()) {
                if (!mOriginalStudents.containsKey(personId)) {
                    System.out.printf("Student %s has colleague preference %s, but we are unable to comply - CNP not found!\n", student.getFullName(), personId);
                    continue;
                    //} else if (!mOriginalStudents.get(personId).getPreferedColleaguesIds().contains(student.getCnp())) {
                    //    System.out.printf("Student %s has colleague preference %s, but we are unable to comply - preference not mutual!\n", student.getFullName(), personId);
                    //    continue;
                } else if (student.getGender() != mOriginalStudents.get(personId).getGender()) {
                    System.out.printf("Student %s has colleague preference %s, but we are unable to comply - wrong gender!\n", student.getFullName(), personId);
                    continue;
                }

                student.addColleaguePreference(mOriginalStudents.get(personId));
                if (mOriginalStudents.get(personId).getAccommodation().isPresent()) {
                    student.addAccommodationPreference(mOriginalStudents.get(personId).getAccommodation().get());
                }
            }
        }

        for (var b : mBuildings.values()) {
            for (var r : b.getRooms()) {
                System.out.printf("Room %s is prefered by: %s\n", r, r.getPreferedBy());
            }
        }
         */


    }

}
