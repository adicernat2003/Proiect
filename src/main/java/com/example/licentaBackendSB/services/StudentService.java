package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.managers.Manager;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.*;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.repositories.*;
import com.example.licentaBackendSB.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final static Random random = new Random();

    private final StudentRepository studentRepository;
    private final StudentAccountRepository studentAccountRepository;
    private final StudentConverter studentConverter;
    private final StudentAccountService studentAccountService;
    private final CaminRepository caminRepository;
    private final Manager manager;
    private final StringUtils stringUtils;
    private final CameraRepository cameraRepository;
    private final PreferintaRepository preferintaRepository;
    private final CameraService cameraService;
    private final CaminService caminService;

    public List<StudentDto> getStudentsByAnUniversitar(Integer anUniversitar) {
        return studentRepository.findAllByAnUniversitar(anUniversitar)
                .stream()
                .map(studentConverter::mapStudentEntityToDto)
                .sorted(Comparator.comparing(StudentDto::getMedie).reversed())
                .toList();
    }

    public Student findStudentByNameAndSurnameAndAnUniversitar(StudentAccount studentAccount, Integer anUniversitar) {
        Optional<Student> foundStudent = studentRepository.getStudentByNumeAndPrenumeAndAnUniversitar(studentAccount.getNume(), studentAccount.getPrenume(), anUniversitar);
        return foundStudent.orElseThrow(() -> new IllegalStateException("Student doesn't exist!"));
    }

    public void deleteStudent(long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            int numberOfStudentsWithGivenCnp = studentRepository.countAllByCnp(studentOptional.get().getCnp());
            if (numberOfStudentsWithGivenCnp > 1) {
                studentRepository.deleteById(studentId);
            } else {
                Optional<StudentAccount> studentAccountOptional = studentAccountRepository.findByCnp(studentOptional.get().getCnp());
                if (studentAccountOptional.isPresent()) {
                    StudentAccount studentAccount = studentAccountOptional.get();
                    studentAccount.setIsActive(Boolean.FALSE);
                    studentAccountRepository.save(studentAccount);
                    studentRepository.deleteById(studentId);
                } else {
                    throw new IllegalStateException("StudentAccount with id " + studentId + " doesn't exist");
                }
            }
        } else {
            throw new IllegalStateException("Student with id " + studentId + " doesn't exist");
        }
    }

    public Student getStudentById(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            return studentOptional.get();
        }
        throw new IllegalArgumentException("Invalid student Id:" + studentId);
    }

    public List<Student> getStudentsByCaminAndAnUniversitar(String numeCamin, String anUniversitar) {
        Optional<Camin> caminOpt = caminRepository.findCaminByNumeCaminAndAnUniversitar(numeCamin, Integer.parseInt(anUniversitar));
        if (caminOpt.isPresent()) {
            Camin camin = caminOpt.get();
            return studentRepository.findAllByCaminAndAnUniversitar(camin, Integer.parseInt(anUniversitar));
        }
        throw new IllegalArgumentException("Acest camin nu exista!");
    }

    public Integer getLowestAnUniversitarForStudent(String nume, String prenume) {
        Optional<Integer> lowestAnUniversitarForStudentOpt = studentRepository.getLowestAnUniversitarForStudent(nume, prenume);
        if (lowestAnUniversitarForStudentOpt.isPresent()) {
            return lowestAnUniversitarForStudentOpt.get();
        }
        throw new IllegalArgumentException("Student does not exist");
    }

    public void updateStudent(Long studentId, StudentDto newStudent) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            Student foundStudent = studentOptional.get();
            String studentCnpBeforeUpdate = foundStudent.getCnp();

            if (newStudent.getNume() != null
                    && newStudent.getNume().length() > 0
                    && !foundStudent.getNume().equals(newStudent.getNume())) {
                foundStudent.setNume(newStudent.getNume());
            }

            if (newStudent.getPrenume() != null
                    && newStudent.getPrenume().length() > 0
                    && !foundStudent.getPrenume().equals(newStudent.getPrenume())) {
                foundStudent.setPrenume(newStudent.getPrenume());
            }

            if (newStudent.getCnp() != null
                    && newStudent.getCnp().length() > 0
                    && !foundStudent.getCnp().equals(newStudent.getCnp())) {
                foundStudent.setCnp(newStudent.getCnp());
            }

            if (newStudent.getZi_de_nastere() != null
                    && newStudent.getZi_de_nastere().length() > 0
                    && !foundStudent.getZi_de_nastere().equals(newStudent.getZi_de_nastere())) {
                foundStudent.setZi_de_nastere(newStudent.getZi_de_nastere());
            }

            if (newStudent.getAn() != null
                    && !foundStudent.getAn().equals(newStudent.getAn())) {
                foundStudent.setAn(newStudent.getAn());
            }

            if (newStudent.getGrupa() != null
                    && newStudent.getGrupa().length() > 0
                    && !foundStudent.getGrupa().equals(newStudent.getGrupa())) {
                foundStudent.setGrupa(newStudent.getGrupa());
            }

            if (newStudent.getSerie() != null
                    && newStudent.getSerie().length() > 0
                    && !foundStudent.getSerie().equals(newStudent.getSerie())) {
                foundStudent.setSerie(newStudent.getSerie());
            }

            if (newStudent.getJudet() != null
                    && newStudent.getJudet().length() > 0
                    && !foundStudent.getJudet().equals(newStudent.getJudet())) {
                foundStudent.setJudet(newStudent.getJudet());
            }

            studentAccountService.updateStudent(studentCnpBeforeUpdate, newStudent);
            studentRepository.save(foundStudent);
        } else {
            throw new IllegalStateException("Student with id " + studentId + " does not exist");
        }
    }

    public String getMyPage(String anUniversitar, Model model) {
        LoggedAccount loggedAccount = new LoggedAccount();

        if (loggedAccount.checkIfStandardAccLogged()) {
            model.addAttribute("devUsernameAccount", loggedAccount.getLoggedUsername());
        } else {
            StudentAccount loggedStudentAccount = studentAccountService.getLoggedStudentAccount();

            Student infoStudent = this.findStudentByNameAndSurnameAndAnUniversitar(loggedStudentAccount, Integer.parseInt(anUniversitar));

            model.addAttribute("loggedStudentAccount", loggedStudentAccount);
            model.addAttribute("infoStudent", infoStudent);
            model.addAttribute("isDevAcc", loggedAccount.checkIfStandardAccLogged().toString());

            model.addAttribute("selectedYears", manager.getListOfYearsForStudentPage(infoStudent, Integer.parseInt(anUniversitar)));
            model.addAttribute("anCurent", anUniversitar);
            model.addAttribute("anUniversitar", anUniversitar);
            model.addAttribute("friends", studentRepository.findAllFriendsOfStudent(infoStudent.getId()).stream().map(Student::getFullName).toList());
            model.addAttribute("optiuniCamere", stringUtils.mapCamereToNumarCamere(preferintaRepository.findAllPreferencesOfStudent(infoStudent.getId())));
            model.addAttribute("camineNedorite", caminRepository.getAllUndesiredCamineOfStudent(infoStudent.getId()).stream().map(Camin::getNumeCamin).toList());
        }
        return "pages/layer 4/info pages/student/mypage";
    }

    public void updateFlag(Long studentId) {
        Optional<Student> foundStudentOptional = studentRepository.findById(studentId);
        if (foundStudentOptional.isPresent()) {
            Student foundStudent = foundStudentOptional.get();
            foundStudent.setIsCazSpecial(Boolean.FALSE.equals(foundStudent.getIsCazSpecial()) ? Boolean.TRUE : Boolean.FALSE);
        } else {
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
    }

    public String redirectToMyPage(String anUniversitar) {
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String editOptiuniCamereCamin(Long studentId, Model model) {
        Student selectedStudent = this.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("listOfCamerePreferate", manager.getListOfCamerePreferateForStudent(selectedStudent));
        return "pages/layer 4/info pages/student/crud mypage/update_optiuni_camere";
    }

    public String updateOptiuniCamere(Long studentId, String anUniversitar, StudentDto newStudent, boolean addAnotherOptiuneCamera) {
        Student student = this.getStudentById(studentId);
        if (newStudent.getCamerePreferate() != null && !newStudent.getCamerePreferate().isEmpty()) {
            for (String numeCamera : newStudent.getCamerePreferate()) {
                Camera camera = cameraRepository.findByNumarCameraAndAnUniversitar(stringUtils.extractNumeCameraFromString(numeCamera), Integer.parseInt(anUniversitar));
                this.addAccommodationPreference(camera.getId(), studentId);
            }
            studentRepository.save(student);
            if (Boolean.TRUE.equals(addAnotherOptiuneCamera)) {
                return "redirect:/student/mypage/camere-edit/" + studentId;
            }
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String clearCamerePreferate(Long studentId, String numarCamera, String anUniversitar, boolean clearAllCamerePreferate) {
        if (Boolean.TRUE.equals(clearAllCamerePreferate)) {
            List<Preferinta> preferinte = preferintaRepository.findAllPreferencesOfStudent(studentId);
            for (Preferinta preferinta : preferinte) {
                List<Camera> camere = cameraRepository.getAllCamereByPreferinta(preferinta.getId());
                for (Camera camera : camere) {
                    cameraRepository.deleteRowFromStudentCameraPreferata(camera.getId(), studentId);
                }
            }
            preferintaRepository.deleteAll(preferinte);
            return "redirect:/student/mypage/" + anUniversitar;
        } else {
            Camera camera = cameraRepository.findByNumarCameraAndAnUniversitar(numarCamera.split(",")[0], Integer.parseInt(anUniversitar));
            List<Preferinta> preferinte = preferintaRepository.findAllPreferencesOfStudent(studentId);
            for (Preferinta preferinta : preferinte) {
                preferintaRepository.deleteRowFromPreferintaCamera(preferinta.getId(), camera.getId());
            }
            cameraRepository.deleteRowFromStudentCameraPreferata(camera.getId(), studentId);
            return "redirect:/student/mypage/camere-edit/" + studentId;
        }
    }

    public String editFriendTokens(Long studentId, Model model) {
        Student selectedStudent = this.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("listOfFriends", manager.getListOfStudentsForStudent(selectedStudent));
        return "pages/layer 4/info pages/student/crud mypage/update_friend_tokens";
    }

    public String updateFriendTokens(Long studentId, String anUniversitar, StudentDto newStudent, boolean addAnotherFriend) {
        Student student = this.getStudentById(studentId);
        if (newStudent.getFriends() != null && !newStudent.getFriends().isEmpty()) {
            for (String friend : newStudent.getFriends()) {
                String[] numeSiPrenume = friend.split(" ");
                Student studentFriend = studentRepository.getStudentByNumeAndPrenumeAndAnUniversitar(numeSiPrenume[0], numeSiPrenume[1], Integer.parseInt(anUniversitar)).orElse(null);
                student.getFriends().add(studentFriend);
                Objects.requireNonNull(studentFriend).getFriends().add(student);
                studentRepository.save(studentFriend);
            }
            student = studentRepository.save(student);
            this.makeFriends(student);
            if (Boolean.TRUE.equals(addAnotherFriend)) {
                return "redirect:/student/mypage/friend-tokens-edit/" + studentId;
            }
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    private void makeFriends(Student student) {
        List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
        for (Student friend : friends) {
            for (Student otherFriend : student.getFriends()) {
                if (!friend.getId().equals(otherFriend.getId()) && !isFriendAlreadyInStudentsFriendList(friend, otherFriend)) {
                    friend.getFriends().add(otherFriend);
                }
            }
            studentRepository.save(friend);
        }
    }

    private boolean isFriendAlreadyInStudentsFriendList(Student student, Student friend) {
        if (student.getFriends().size() == 0) {
            return false;
        }
        return student.getFriends()
                .stream()
                .anyMatch(localFriend -> localFriend.getId().equals(friend.getId()));
    }

    public String clearFriendTokens(Long studentId, String numePrenumeStudent, String anUniversitar, boolean clearAllFriends) {
        Student student = this.getStudentById(studentId);
        if (Boolean.TRUE.equals(clearAllFriends)) {
            List<Student> friends = studentRepository.findAllFriendsOfStudent(studentId);
            for (Student friend : friends) {
                friend.getFriends().clear();
                studentRepository.save(friend);
            }
            student.getFriends().clear();
            studentRepository.save(student);
            return "redirect:/student/mypage/" + anUniversitar;
        } else {
            List<String> numePrenumeStudentList = Arrays.stream(numePrenumeStudent.split(" ")).toList();
            Long studentTobeDeletedId = Objects.requireNonNull(studentRepository.getStudentByNumeAndPrenumeAndAnUniversitar(numePrenumeStudentList.get(0),
                    numePrenumeStudentList.get(1), Integer.parseInt(anUniversitar)).orElse(null)).getId();
            this.deleteStudent(student, studentTobeDeletedId);
            return "redirect:/student/mypage/friend-tokens-edit/" + studentId;
        }
    }

    private void deleteStudent(Student student, Long idOfStudentToBeDeleted) {
        Student studentToBeDeleted = studentRepository.getById(idOfStudentToBeDeleted);
        student.getFriends().remove(studentToBeDeleted);
        studentRepository.save(student);
        studentToBeDeleted.getFriends().clear();
        studentToBeDeleted.getPreferinte().clear();
        List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
        for (Student friend : friends) {
            friend.getFriends().remove(studentToBeDeleted);
            studentRepository.save(friend);
        }
        studentRepository.save(studentToBeDeleted);
    }

    public void setAccommodation(Long studentId, Long accommodationId) {

        Student student = studentRepository.getById(studentId);
        Camera accommodation = cameraRepository.getById(accommodationId);

        Camera finalAccommodation1 = accommodation;
        if (student.getCameraRepartizata() != null && accommodation.getCamin().equals(student.getCameraRepartizata().getCamin())
                && cameraRepository.getAllCamerePreferateOfStudent(studentId).stream()
                .noneMatch(camera -> camera.equals(finalAccommodation1))) {
            log.info("No point in moving {} from {} to {}\n", student.getFullName(), student.getCameraRepartizata().getNumarCamera(), accommodation.getNumarCamera());
            return;
        }

        Camera finalAccommodation = accommodation;
        if (student.getCameraRepartizata() != null && (!accommodation.getCamin().equals(student.getCameraRepartizata().getCamin()) ||
                (accommodation.getCamin().equals(student.getCameraRepartizata().getCamin())
                        && cameraRepository.getAllCamerePreferateOfStudent(studentId).stream()
                        .anyMatch(camera -> camera.equals(finalAccommodation))))) {
            log.info("Moving {} from {} to {}...\n", student.getFullName(), student.getCameraRepartizata().getNumarCamera(), accommodation.getNumarCamera());
            accommodation = cameraService.removeStudent(accommodation.getId(), student);
        }

        cameraService.assignStudent(accommodationId, student);

        for (Preferinta preferinta : preferintaRepository.findAllPreferencesOfStudent(studentId)) {
            preferinta = preferintaRepository.getById(preferinta.getId());
            List<Camera> camere = cameraService.getAllCamereOfPreferinta(preferinta);
            for (Camera camera : camere) {
                camera = cameraRepository.getById(camera.getId());
                cameraService.removePreference(camera.getId(), student);
            }
        }

        log.info("Camera " + accommodation.getNumarCamera() + " are acum " + cameraRepository.getAllStudentsAccommodatedToCamera(accommodation.getId()).size() + " studenti cazati!");
        log.info("Studentii cazati sunt: " + cameraRepository.getAllStudentsAccommodatedToCamera(accommodation.getId()));
    }

    public Student addAccommodationPreference(Long cameraId, Long studentId) {
        Student student = studentRepository.getById(studentId);
        Camera camera = cameraService.setPrefferedBy(cameraId, student);

        List<Preferinta> preferinte = preferintaRepository.findAllPreferencesOfStudent(student.getId());
        for (Preferinta preferinta : preferinte) {
            preferinta = preferintaRepository.getById(preferinta.getId());
            if (caminRepository.getCaminOfCamera(cameraId).equals(caminService.getCaminOfPreferinta(preferinta))) {
                cameraRepository.insertIntoPreferintaCamera(preferinta.getId(), cameraId);
                log.info(preferinta.getId() + " interior " + camera.getId());
                return student;
            }
        }
        Preferinta preferintaNoua = preferintaRepository.save(new Preferinta(student, List.of(camera)));
        preferintaRepository.updateCaminOfPreferinta(caminRepository.getCaminIdOfCamera(cameraId), preferintaNoua.getId());
        preferintaNoua = preferintaRepository.getById(preferintaNoua.getId());
        student.getPreferinte().put(caminService.getCaminOfPreferinta(preferintaNoua), preferintaNoua);
        log.info(preferintaNoua.getId() + " exterior " + camera.getId());
        return studentRepository.save(student);
    }

    public void addPriorityAccommodationPreference(Long caminId, Long cameraId, Long studentId) {
        Camin camin = caminRepository.getById(caminId);
        Student student = studentRepository.getById(studentId);
        Camera camera = cameraService.setPrefferedBy(cameraId, student);
        Preferinta preferinta = null;
        List<Preferinta> preferinte = preferintaRepository.findAllPreferencesOfStudent(student.getId());

        for (Preferinta p : preferinte) {
            if (camin.equals(caminService.getCaminOfPreferinta(p))) {
                p.getCamere().add(0, camera);
                preferinta = p;
                preferintaRepository.save(p);
                break;
            }
        }

        if (preferinta != null) {
            preferinte.remove(preferinta);
            preferinte.add(0, preferinta);
            preferintaRepository.saveAll(preferinte);
        } else {
            preferinte.add(0, new Preferinta(student, List.of(camera)));
            preferintaRepository.saveAll(preferinte);
        }
    }

    public void makeFriends(String anUniversitar) {
        List<Long> studentIdsWithNoFriendsYet = this.getStudentsWithNoFriendsYet(anUniversitar);
        for (Long studentId : studentIdsWithNoFriendsYet) {
            int randomNumberOfFriends = random.nextInt(4);

            List<Student> studentsWithNoFriendsYetWithSameGenderAsSelectedStudent = this.getStudentsWithNoFriendsYet(studentId, studentIdsWithNoFriendsYet);

            if (studentsWithNoFriendsYetWithSameGenderAsSelectedStudent.size() < randomNumberOfFriends) {
                randomNumberOfFriends = studentsWithNoFriendsYetWithSameGenderAsSelectedStudent.size();
            }

            for (int i = 0; i < randomNumberOfFriends; i++) {
                Student student = studentRepository.getById(studentId);
                if (randomNumberOfFriends <= student.getFriends().size()) {
                    break;
                }

                Student studentFriend = studentsWithNoFriendsYetWithSameGenderAsSelectedStudent.get(0);

                student.getFriends().add(studentFriend);
                studentFriend.getFriends().add(student);
                studentRepository.save(studentFriend);
                student = studentRepository.save(student);

                this.makeFriends(student);

                Student finalStudent = student;
                studentsWithNoFriendsYetWithSameGenderAsSelectedStudent = studentsWithNoFriendsYetWithSameGenderAsSelectedStudent.stream()
                        .filter(s -> s.getFriends().size() == 0 && !finalStudent.getFriends().contains(s))
                        .toList();
            }
        }
    }

    private List<Long> getStudentsWithNoFriendsYet(String anUniversitar) {
        return studentRepository.findAllByAnUniversitar(Integer.parseInt(anUniversitar))
                .stream()
                .filter(student -> student.getFriends().size() == 0)
                .map(BaseEntityForIdsAndAnUniversitar::getId)
                .toList();
    }

    private List<Student> getStudentsWithNoFriendsYet(Long studentId, List<Long> studentIdsWithNoFriendsYet) {
        Student student = studentRepository.getById(studentId);
        return studentIdsWithNoFriendsYet.stream()
                .map(studentRepository::getById)
                .filter(s -> student.getGenSexual().equals(s.getGenSexual()) && !s.equals(student) && s.getFriends().size() == 0)
                .toList();
    }

    public void insertRandomUndesiredCamineForEachStudent(String anUniversitar) {
        List<Long> studentIds = studentRepository.findAllIdsOfStudendsByAnUniversitar(Integer.parseInt(anUniversitar));
        List<Camin> camine = caminRepository.findAllByAnUniversitar(Integer.parseInt(anUniversitar));
        for (Long studentId : studentIds) {
            int randomNumberOfCamine = random.nextInt(camine.size());
            Student student = studentRepository.getById(studentId);
            while (randomNumberOfCamine > 0) {
                if (Boolean.TRUE.equals(student.getAlreadySelectedUndesiredCamine())) {
                    break;
                }
                Camin randomCamin = camine.get(random.nextInt(camine.size()));

                if (caminRepository.getAllUndesiredCamineOfStudent(student.getId()).contains(randomCamin)
                        || caminRepository.getAllCamineOfStudentPreferences(student.getId()).contains(randomCamin)) {
                    randomNumberOfCamine--;
                    continue;
                }

                studentRepository.insertRowIntoStudentUndesiredAccomodation(studentId, randomCamin.getId());

                randomNumberOfCamine--;
            }

            student = studentRepository.getById(studentId);

            if (Boolean.FALSE.equals(student.getAlreadySelectedUndesiredCamine()) && randomNumberOfCamine > 0) {
                student.setAlreadySelectedUndesiredCamine(true);
            }
            studentRepository.save(student);
        }
    }

    public void insertRandomPreferinteForEachStudent(String anUniversitar) {
        List<Long> studentIds = studentRepository.findAllIdsOfStudendsByAnUniversitar(Integer.parseInt(anUniversitar));
        List<Camin> camine = caminRepository.findAllByAnUniversitar(Integer.parseInt(anUniversitar));
        for (Long studentId : studentIds) {
            int randomNumberOfPreferences = random.nextInt(6);
            Student student = studentRepository.getById(studentId);
            while (randomNumberOfPreferences > 0) {
                if (Boolean.TRUE.equals(student.getAlreadySelectedPreferences())) {
                    break;
                }

                Camin randomCamin = caminRepository.getById(camine.get(random.nextInt(camine.size())).getId());

                if (caminRepository.getAllUndesiredCamineOfStudent(student.getId()).contains(randomCamin)) {
                    randomNumberOfPreferences--;
                    continue;
                }

                Camera randomCamera = cameraRepository.getById(cameraRepository.findAllByCaminId(randomCamin.getId())
                        .get(random.nextInt((int) cameraRepository.countAllByCaminId(randomCamin.getId()))).getId());

                if (cameraRepository.getAllCamerePreferateOfStudent(student.getId()).contains(randomCamera)) {
                    randomNumberOfPreferences--;
                    continue;
                }

                student = this.addAccommodationPreference(randomCamera.getId(), student.getId());

                randomNumberOfPreferences--;
            }
            if (Boolean.FALSE.equals(student.getAlreadySelectedPreferences()) && randomNumberOfPreferences > 0) {
                student.setAlreadySelectedPreferences(true);
            }
            studentRepository.save(student);
        }
    }

    public String editCamineNedorite(Long studentId, Model model) {
        Student selectedStudent = this.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("listOfCamineNedorite", manager.getListOfCamineNedoriteForStudent(selectedStudent));
        return "pages/layer 4/info pages/student/crud mypage/update_camine_nedorite";
    }

    public String updateCamineNedorite(Long studentId,
                                       String anUniversitar,
                                       StudentDto newStudent,
                                       boolean addAnotherCaminNedorit) {
        if (newStudent.getCamineNedorite() != null && !newStudent.getCamineNedorite().isEmpty()) {
            for (String numeCaminNedorit : newStudent.getCamineNedorite()) {
                Camin caminNedorit = caminRepository.findCaminByNumeCaminAndAnUniversitar(numeCaminNedorit,
                        Integer.parseInt(anUniversitar)).orElse(null);
                studentRepository.insertRowIntoStudentUndesiredAccomodation(studentId, Objects.requireNonNull(caminNedorit).getId());
            }
            if (Boolean.FALSE.equals(this.getStudentById(studentId).getAlreadySelectedUndesiredCamine())) {
                studentRepository.setAlreadySelectedUndesiredCamineOfStudent(true, studentId);
            }
            if (Boolean.TRUE.equals(addAnotherCaminNedorit)) {
                return "redirect:/student/mypage/camine-nedorite-edit/" + studentId;
            }
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String clearCamineNedorite(Long studentId,
                                      String numeCaminNedorit,
                                      String anUniversitar,
                                      boolean clearAllCamineNedorite) {
        if (Boolean.TRUE.equals(clearAllCamineNedorite)) {
            studentRepository.deleteAllUndesiredCamineOfStudent(studentId);
            studentRepository.setAlreadySelectedUndesiredCamineOfStudent(false, studentId);
            return "redirect:/student/mypage/" + anUniversitar;
        } else {
            Camin caminNedorit = caminRepository.findCaminByNumeCaminAndAnUniversitar(numeCaminNedorit,
                    Integer.parseInt(anUniversitar)).orElse(null);
            studentRepository.deleteUndesiredCaminOfStudent(studentId, Objects.requireNonNull(caminNedorit).getId());
            if (caminRepository.getAllUndesiredCamineOfStudent(studentId).size() == 0) {
                studentRepository.setAlreadySelectedUndesiredCamineOfStudent(false, studentId);
            }
            return "redirect:/student/mypage/camine-nedorite-edit/" + studentId;
        }
    }

}