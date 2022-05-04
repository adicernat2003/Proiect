package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.managers.Manager;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.others.LoggedAccount;
import com.example.licentaBackendSB.repositories.CameraRepository;
import com.example.licentaBackendSB.repositories.CaminRepository;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import com.example.licentaBackendSB.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    //Fields
    private final StudentRepository studentRepository;
    private final StudentAccountRepository studentAccountRepository;
    private final StudentConverter studentConverter;
    private final StudentAccountService studentAccountService;
    private final CaminRepository caminRepository;
    private final Manager manager;
    private final StringUtils stringUtils;
    private final CameraRepository cameraRepository;

    //Methods
    /*  ~~~~~~~~~~~ Get List of Students ~~~~~~~~~~~ */
    public List<StudentDto> getStudentsByAnUniversitar(Integer anUniversitar) {
        //select * from student (query in DB)
        //sortam lista care vine din DB
        return studentRepository.findAllByAnUniversitar(anUniversitar)
                .stream()
                .map(studentConverter::mapStudentEntityToDto)
                .sorted(Comparator.comparing(StudentDto::getMedie).reversed())
                .toList();
    }

    /*  ~~~~~~~~~~~ Find Student by Name and Surname ~~~~~~~~~~~ */
    public Student findStudentByNameAndSurnameAndAnUniversitar(StudentAccount studentAccount, Integer anUniversitar) {
        Optional<Student> foundStudent = studentRepository.getStudentByNumeAndPrenumeAndAnUniversitar(studentAccount.getNume(), studentAccount.getPrenume(), anUniversitar);
        return foundStudent
                .orElseThrow(() -> new IllegalStateException("Student doesn't exist!"));
    }

    /*  ~~~~~~~~~~~ Add new Student ~~~~~~~~~~~ */
    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentRepository.getStudentByNumeAndPrenumeAndAnUniversitar(student.getNume(), student.getPrenume(), student.getAnUniversitar());

        //daca studentul cu exista cu numele respectiv, aruncam exceptie
        if (studentOptional.isPresent()) {
            throw new IllegalStateException("Student already exists");
        }
        //implicit daca nu exista, il salvam in db
        studentRepository.save(student);
    }

    /*  ~~~~~~~~~~~ Delete Student from Student Table ~~~~~~~~~~~ */
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

    /*  ~~~~~~~~~~~ Get Id of Student to update Student && FriendToken ~~~~~~~~~~~ */
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

    /*  ~~~~~~~~~~~ Update Student ~~~~~~~~~~~ */
    public void updateStudent(Long studentId, StudentDto newStudent) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            Student foundStudent = studentOptional.get();
            String studentCnpBeforeUpdate = foundStudent.getCnp();
            /** update nume*/
            if (newStudent.getNume() != null
                    && newStudent.getNume().length() > 0
                    && !foundStudent.getNume().equals(newStudent.getNume())) {
                foundStudent.setNume(newStudent.getNume());
            }

            /** update prenume*/
            if (newStudent.getPrenume() != null
                    && newStudent.getPrenume().length() > 0
                    && !foundStudent.getPrenume().equals(newStudent.getPrenume())) {
                foundStudent.setPrenume(newStudent.getPrenume());
            }

            /** update cnp*/
            if (newStudent.getCnp() != null
                    && newStudent.getCnp().length() > 0
                    && !foundStudent.getCnp().equals(newStudent.getCnp())) {
                foundStudent.setCnp(newStudent.getCnp());
            }

            /** update zi_de_nastere*/
            if (newStudent.getZi_de_nastere() != null
                    && newStudent.getZi_de_nastere().length() > 0
                    && !foundStudent.getZi_de_nastere().equals(newStudent.getZi_de_nastere())) {
                foundStudent.setZi_de_nastere(newStudent.getZi_de_nastere());
            }

            /** update an*/
            if (newStudent.getAn() != null
                    && !foundStudent.getAn().equals(newStudent.getAn())) {
                foundStudent.setAn(newStudent.getAn());
            }

            /** update grupa*/
            if (newStudent.getGrupa() != null
                    && newStudent.getGrupa().length() > 0
                    && !foundStudent.getGrupa().equals(newStudent.getGrupa())) {
                foundStudent.setGrupa(newStudent.getGrupa());
            }

            /** update serie*/
            if (newStudent.getSerie() != null
                    && newStudent.getSerie().length() > 0
                    && !foundStudent.getSerie().equals(newStudent.getSerie())) {
                foundStudent.setSerie(newStudent.getSerie());
            }

            /** update judet*/
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
            //query call in db to get info of logged student
            Student infoStudent = this.findStudentByNameAndSurnameAndAnUniversitar(loggedStudentAccount, Integer.parseInt(anUniversitar));

            //getting info about logged acc (credentials) && student info
            model.addAttribute("loggedStudentAccount", loggedStudentAccount);
            model.addAttribute("infoStudent", infoStudent);
            //checkup in case we log in with a dev account
            model.addAttribute("isDevAcc", loggedAccount.checkIfStandardAccLogged().toString());

            model.addAttribute("selectedYears", manager.getListOfYearsForStudentPage(infoStudent, Integer.parseInt(anUniversitar)));
            model.addAttribute("anCurent", anUniversitar);
            model.addAttribute("anUniversitar", anUniversitar);
            model.addAttribute("optiuniCamine", stringUtils.mapCamineToNumeCamie(infoStudent.getCaminePreferate()));
            model.addAttribute("optiuniCamere", stringUtils.mapCamereToNumarCamere(infoStudent.getCamerePreferate()));
        }
        return "pages/layer 4/info pages/student/mypage";
    }

    /*  ~~~~~~~~~~~ Update Flag from Nu to Da and reverse ~~~~~~~~~~~ */
    public void updateFlag(Long studentId) {
        Optional<Student> foundStudentOptional = studentRepository.findById(studentId);
        if (foundStudentOptional.isPresent()) {
            Student foundStudent = foundStudentOptional.get();
            foundStudent.setIsCazSpecial(Boolean.FALSE.equals(foundStudent.getIsCazSpecial()) ? Boolean.TRUE : Boolean.FALSE);
        } else {
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
    }

    /*
        public String validateNumberOfFriendsBasedOnMaximumNumberOfStudentiInCamera(Student student, StudentDto studentDto) {
        String message = "";
        if (studentDto.getFriends() == null) {
            return message;
        }
        int maxNumberOfStudentiInCamera = manager.getMaxNumberOfStudentiInCamera(student);
        if (maxNumberOfStudentiInCamera - 1 <= studentDto.getFriends().size()) {
            int nrStudenti = (1 + studentDto.getFriends().size());
            message = "Nu pot sta " + nrStudenti + " studenti intr-o camera de maxim " + maxNumberOfStudentiInCamera + " locuri!";
        }
        return message;
    }
     */

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
                student.getCamerePreferate().add(camera);
            }
            studentRepository.save(student);
            this.refreshCamerePreferateToAllStudentFriends(student);
            if (Boolean.TRUE.equals(addAnotherOptiuneCamera)) {
                return "redirect:/student/mypage/camere-edit/" + studentId;
            }
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String clearCamerePreferate(Long studentId, String indexOptiuneString, String anUniversitar, boolean clearAllCamerePreferate) {
        Student student = this.getStudentById(studentId);
        if (Boolean.TRUE.equals(clearAllCamerePreferate)) {
            student.getCamerePreferate().clear();
            studentRepository.save(student);
            this.refreshCamerePreferateToAllStudentFriends(student);
            return "redirect:/student/mypage/" + anUniversitar;
        } else {
            int indexOptiuneInteger = Integer.parseInt(indexOptiuneString);
            student.getCamerePreferate().remove(indexOptiuneInteger);
            studentRepository.save(student);
            this.refreshCamerePreferateToAllStudentFriends(student);
            return "redirect:/student/mypage/camere-edit/" + studentId;
        }
    }

    public String editFriendTokens(Long studentId, Model model) {
        Student selectedStudent = this.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("listOfFriends", manager.getListOfStudentsForStudent(selectedStudent));
        model.addAttribute("firstOption", studentDto.getFriends().size() > 0 ? studentDto.getFriends().get(0) : null);
        model.addAttribute("secondOption", studentDto.getFriends().size() > 1 ? studentDto.getFriends().get(1) : null);
        model.addAttribute("thirdOption", studentDto.getFriends().size() > 2 ? studentDto.getFriends().get(2) : null);
        model.addAttribute("fourthOption", studentDto.getFriends().size() > 3 ? studentDto.getFriends().get(3) : null);
        return "pages/layer 4/info pages/student/crud mypage/update_friend_tokens";
    }

    public String updateFriendTokens(Long studentId, String anUniversitar, StudentDto newStudent, boolean addAnotherFriend) {
        Student student = this.getStudentById(studentId);
        if (newStudent.getFriends() != null && !newStudent.getFriends().isEmpty()) {
            for (String friend : newStudent.getFriends()) {
                String[] numeSiPrenume = friend.split(" ");
                Student studentFriend = studentRepository.getStudentByNumeAndPrenumeAndAnUniversitar(numeSiPrenume[0], numeSiPrenume[1], Integer.parseInt(anUniversitar)).get();
                student.getFriends().add(studentFriend);
                studentFriend.getFriends().add(student);
                studentRepository.save(studentFriend);
            }
            student = studentRepository.save(student);
            this.makeFriends(student);
            this.refreshCamereAndCaminePreferateForAllStudentFriends(student);
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

    public String clearFriendTokens(Long studentId, String indexOptiuneString, String anUniversitar, boolean clearAllFriends) {
        Student student = this.getStudentById(studentId);
        if (Boolean.TRUE.equals(clearAllFriends)) {
            List<Student> friends = studentRepository.findAllFriendsOfStudent(studentId);
            for (Student friend : friends) {
                friend.getFriends().clear();
                studentRepository.save(friend);
            }
            student.getFriends().clear();
            studentRepository.save(student);
            this.refreshCamereAndCaminePreferateForAllStudentFriends(student);
            return "redirect:/student/mypage/" + anUniversitar;
        } else {
            int indexOptiuneInteger = Integer.parseInt(indexOptiuneString);
            this.deleteStudent(student, student.getFriends().get(indexOptiuneInteger).getId(), indexOptiuneInteger);
            return "redirect:/student/mypage/friend-tokens-edit/" + studentId;
        }
    }

    private void deleteStudent(Student student, Long idOfStudentToBeDeleted, int indexOptiuneInteger) {
        Student studentToBeDeleted = studentRepository.getById(idOfStudentToBeDeleted);
        student.getFriends().remove(indexOptiuneInteger);
        studentRepository.save(student);
        studentToBeDeleted.getFriends().clear();
        studentToBeDeleted.getCaminePreferate().clear();
        studentToBeDeleted.getCamerePreferate().clear();
        List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
        for (Student friend : friends) {
            friend.getFriends().remove(studentToBeDeleted);
            studentRepository.save(friend);
        }
        studentRepository.save(studentToBeDeleted);
    }

    public String editCaminePreferate(Long studentId, Model model) {
        Student selectedStudent = this.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("listOfCamine", manager.getListOfCaminePreferate(selectedStudent));
        return "pages/layer 4/info pages/student/crud mypage/update_optiuni_camine";
    }

    public String updateCaminePreferate(Long studentId, String anUniversitar, StudentDto newStudent, boolean addAnotherCaminPreferat) {
        Student student = this.getStudentById(studentId);
        if (newStudent.getCaminePreferate() != null && !newStudent.getCaminePreferate().isEmpty()) {
            for (String numeCamin : newStudent.getCaminePreferate()) {
                Camin camin = caminRepository.findCaminByNumeCaminAndAnUniversitar(numeCamin, Integer.parseInt(anUniversitar)).get();
                student.getCaminePreferate().add(camin);
            }
            studentRepository.save(student);
            this.refreshCaminePreferateToAllStudentFriends(student);
            if (Boolean.TRUE.equals(addAnotherCaminPreferat)) {
                return "redirect:/student/mypage/camine-edit/" + studentId;
            }
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String clearCaminePreferate(Long studentId, String indexOptiuneString, String anUniversitar, boolean clearAllCaminePreferate) {
        Student student = this.getStudentById(studentId);
        if (Boolean.TRUE.equals(clearAllCaminePreferate)) {
            student.getCaminePreferate().clear();
            studentRepository.save(student);
            this.refreshCaminePreferateToAllStudentFriends(student);
            return "redirect:/student/mypage/" + anUniversitar;
        } else {
            student.getCaminePreferate().remove(Integer.parseInt(indexOptiuneString));
            studentRepository.save(student);
            this.refreshCaminePreferateToAllStudentFriends(student);
            return "redirect:/student/mypage/camine-edit/" + studentId;
        }
    }

    private void refreshCamereAndCaminePreferateForAllStudentFriends(Student student) {
        this.refreshCaminePreferateToAllStudentFriends(student);
        this.refreshCamerePreferateToAllStudentFriends(student);
    }

    private void refreshCaminePreferateToAllStudentFriends(Student student) {
        List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
        for (Student friend : friends) {
            friend.getCaminePreferate().clear();
            List<Camin> caminePreferate = caminRepository.findAllCaminePreferateOfStudent(student.getId());
            for (Camin camin : caminePreferate) {
                friend.getCaminePreferate().add(camin);
            }
            studentRepository.save(friend);
        }
    }

    private void refreshCamerePreferateToAllStudentFriends(Student student) {
        List<Student> friends = studentRepository.findAllFriendsOfStudent(student.getId());
        for (Student friend : friends) {
            friend.getCamerePreferate().clear();
            List<Camera> camerePreferate = cameraRepository.findAllCamerePreferateOfStudent(student.getId());
            for (Camera camera : camerePreferate) {
                friend.getCamerePreferate().add(camera);
            }
            studentRepository.save(friend);
        }
    }

}