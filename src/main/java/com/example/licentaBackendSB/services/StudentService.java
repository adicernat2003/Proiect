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

    private void importOptiuniNumarPersoanePentruCeilaltiPrieteniAdaugati(Student student) {
        List<Student> friends = student.getFriends();
        for (Student friend : friends) {
            friend.getNumarLocuriCamera().clear();
            for (Integer numarPersoaneCamera : student.getNumarLocuriCamera()) {
                if (!friend.getNumarLocuriCamera().contains(numarPersoaneCamera))
                    friend.getNumarLocuriCamera().add(numarPersoaneCamera);
            }
            studentRepository.save(friend);
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

    public String updateOptiuniCamere(Long studentId, String anUniversitar, StudentDto newStudent) {
        Student student = this.getStudentById(studentId);
        if (newStudent.getCamerePreferate() != null) {
            for (String numeCamera : newStudent.getCamerePreferate()) {
                Camera camera = cameraRepository.findByNumarCameraAndAnUniversitar(stringUtils.extractNumeCameraFromString(numeCamera), Integer.parseInt(anUniversitar));
                student.getCamerePreferate().add(camera);
            }
            studentRepository.save(student);
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String addAnotherOptiuneCamera(Long studentId, String anUniversitar, StudentDto newStudent) {
        Student student = this.getStudentById(studentId);
        if (newStudent.getNumarLocuriCamera() != null) {
            newStudent.getNumarLocuriCamera().forEach(camera -> student.getNumarLocuriCamera().add(stringUtils.mapStringNumarPersoaneCameraToInteger(camera)));
            studentRepository.save(student);
            this.importOptiuniNumarPersoanePentruCeilaltiPrieteniAdaugati(student);
            return "redirect:/student/mypage/camere-edit/" + studentId;
        } else {
            return "redirect:/student/mypage/" + anUniversitar;
        }
    }

    public String clearCameraPreferata(Long studentId, String indexOptiuneString, String anUniversitar) {
        Student student = this.getStudentById(studentId);
        int indexOptiuneInteger = Integer.parseInt(indexOptiuneString);
        student.getCamerePreferate().remove(indexOptiuneInteger);
        studentRepository.save(student);
        return "redirect:/student/mypage/camere-edit/" + studentId;
    }

    public String clearCamerePreferate(Long studentId, String anUniversitar) {
        Student student = this.getStudentById(studentId);
        student.getCamerePreferate().clear();
        studentRepository.save(student);
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String editFriendTokens(Long studentId, Model model) {
        Student selectedStudent = this.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("listOfFriends", manager.getListOfStudentsForStudent(selectedStudent));
        model.addAttribute("maxNumberOfStudents", manager.getMaxNumberOfStudentiInCamera(selectedStudent));
        model.addAttribute("firstOption", studentDto.getFriends().size() > 0 ? studentDto.getFriends().get(0) : null);
        model.addAttribute("secondOption", studentDto.getFriends().size() > 1 ? studentDto.getFriends().get(1) : null);
        model.addAttribute("thirdOption", studentDto.getFriends().size() > 2 ? studentDto.getFriends().get(2) : null);
        model.addAttribute("fourthOption", studentDto.getFriends().size() > 3 ? studentDto.getFriends().get(3) : null);
        return "pages/layer 4/info pages/student/crud mypage/update_friend_tokens";
    }

    public String updateFriendTokens(Long studentId, String anUniversitar, StudentDto newStudent) {
        Student student = this.getStudentById(studentId);
        String err = this.validateNumberOfFriendsBasedOnMaximumNumberOfStudentiInCamera(student, newStudent);
        if (!err.isEmpty()) {
            log.info(err);
            return "redirect:/student/mypage/" + anUniversitar;
        }
        if (newStudent.getFriends() != null) {
            for (String friend : newStudent.getFriends()) {
                String[] numeSiPrenume = friend.split(" ");
                Student studentFriend = studentRepository.getStudentByNumeAndPrenumeAndAnUniversitar(numeSiPrenume[0], numeSiPrenume[1], Integer.parseInt(anUniversitar)).get();
                this.makeFriends(student, studentFriend);
            }
            this.importOptiuniNumarPersoanePentruCeilaltiPrieteniAdaugati(student);
        }
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String addAnotherFriend(Long studentId, String anUniversitar, StudentDto newStudent) {
        Student student = this.getStudentById(studentId);
        String err = this.validateNumberOfFriendsBasedOnMaximumNumberOfStudentiInCamera(student, newStudent);
        if (!err.isEmpty()) {
            log.info(err);
            return "redirect:/student/mypage/" + anUniversitar;
        }
        if (newStudent.getFriends() != null) {
            for (String friend : newStudent.getFriends()) {
                String[] numeSiPrenume = friend.split(" ");
                Student studentFriend = studentRepository.getStudentByNumeAndPrenumeAndAnUniversitar(numeSiPrenume[0], numeSiPrenume[1], Integer.parseInt(anUniversitar)).get();
                this.makeFriends(student, studentFriend);
            }
            this.importOptiuniNumarPersoanePentruCeilaltiPrieteniAdaugati(student);
            return "redirect:/student/mypage/friend-tokens-edit/" + studentId;
        } else {
            return "redirect:/student/mypage/" + anUniversitar;
        }
    }

    // Fiecare prieten deja existent il va adauga pe cel nou il lista lui, iar cel nou ii va adauga pe toti ceilalti
    private void makeFriends(Student student, Student studentFriend) {
        // Mai intai noul prieten se imprieteneste cu student-ul principal
        student.getFriends().add(studentFriend);
        studentFriend.getFriends().add(student);
        student = studentRepository.save(student);
        // Apoi cu lista lui de prieteni
        for (Student friend : student.getFriends()) {
            if (!friend.equals(studentFriend)) {
                friend.getFriends().add(studentFriend);
                studentFriend.getFriends().add(friend);
                studentRepository.save(friend);
            }
        }
        studentRepository.save(studentFriend);
    }

    public String deleteFriendToken(Long studentId, String indexOptiuneString) {
        Student student = this.getStudentById(studentId);
        int indexOptiuneInteger = Integer.parseInt(indexOptiuneString);
        student.getFriends().remove(indexOptiuneInteger);
        studentRepository.save(student);
        return "redirect:/student/mypage/friend-tokens-edit/" + studentId;
    }

    public String clearFriendTokens(Long studentId, String anUniversitar) {
        Student student = this.getStudentById(studentId);
        for (Student friend : student.getFriends()) {
            friend.getFriends().clear();
            studentRepository.save(friend);
        }
        student.getFriends().clear();
        studentRepository.save(student);
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public List<Student> getStudentsByCaminAndAnUniversitar(String numeCamin, String anUniversitar) {
        Optional<Camin> caminOpt = caminRepository.findCaminByNumeCaminAndAnUniversitar(numeCamin, Integer.parseInt(anUniversitar));
        if (caminOpt.isPresent()) {
            Camin camin = caminOpt.get();
            return studentRepository.findAllByCaminAndAnUniversitar(camin, Integer.parseInt(anUniversitar));
        }
        throw new IllegalArgumentException("Acest camin nu exista!");
    }

    public String editOptiuniCaminePreferate(Long studentId, Model model) {
        Student selectedStudent = this.getStudentById(studentId);
        StudentDto studentDto = studentConverter.mapStudentEntityToDto(selectedStudent);
        model.addAttribute("selectedStudentById", studentDto);
        model.addAttribute("listOfCamine", manager.getListOfCaminePreferate(selectedStudent));
        model.addAttribute("firstOption", selectedStudent.getCaminePreferate().size() > 0 ?
                selectedStudent.getCaminePreferate().get(0).getNumeCamin() : null);
        model.addAttribute("secondOption", selectedStudent.getCaminePreferate().size() > 1 ?
                selectedStudent.getCaminePreferate().get(1).getNumeCamin() : null);
        model.addAttribute("thirdOption", selectedStudent.getCaminePreferate().size() > 2 ?
                selectedStudent.getCaminePreferate().get(2).getNumeCamin() : null);
        model.addAttribute("fourthOption", selectedStudent.getCaminePreferate().size() > 3 ?
                selectedStudent.getCaminePreferate().get(3).getNumeCamin() : null);
        return "pages/layer 4/info pages/student/crud mypage/update_optiuni_camine";
    }

    public String updateCaminePreferate(Long studentId, String anUniversitar, StudentDto newStudent) {
        Student student = this.getStudentById(studentId);
        if (newStudent.getCaminePreferate() != null) {
            for (String numeCamin : newStudent.getCaminePreferate()) {
                Camin camin = caminRepository.findCaminByNumeCaminAndAnUniversitar(numeCamin, Integer.parseInt(anUniversitar)).get();
                student.getCaminePreferate().add(camin);
            }
        }
        studentRepository.save(student);
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String clearCaminePreferate(Long studentId, String anUniversitar) {
        Student student = this.getStudentById(studentId);
        student.getCaminePreferate().clear();
        studentRepository.save(student);
        return "redirect:/student/mypage/" + anUniversitar;
    }

    public String clearCaminPreferat(Long studentId, String indexOptiuneString) {
        Student student = this.getStudentById(studentId);
        int indexOptiuneInteger = Integer.parseInt(indexOptiuneString);
        student.getCaminePreferate().remove(indexOptiuneInteger);
        studentRepository.save(student);
        return "redirect:/student/mypage/camine-edit/" + studentId;
    }
}