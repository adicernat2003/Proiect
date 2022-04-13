package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.REQUIRED)
public class StudentService {

    //Fields
    private final StudentRepository studentRepository;
    private final StudentAccountRepository studentAccountRepository;
    private final StudentConverter studentConverter;
    private final StudentAccountService studentAccountService;

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
    public Student findStudentByNameAndSurname(StudentAccount studentAccount) {
        Optional<Student> foundStudent = studentRepository.getStudentByNumeAndPrenume(studentAccount.getNume(), studentAccount.getPrenume());

        return foundStudent.orElseThrow(() -> new IllegalStateException("Student doesn't exist!"));
    }

    /*  ~~~~~~~~~~~ Add new Student ~~~~~~~~~~~ */
    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentRepository.getStudentByNumeAndPrenume(student.getNume(), student.getPrenume());

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
        } else {
            throw new IllegalArgumentException("Invalid student Id:" + studentId);
        }
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

    /*  ~~~~~~~~~~~ Update (THIS) with FriendToken ~~~~~~~~~~~ */
    public void updateFriendToken(Long studentId, StudentDto newStudent) {
        studentRepository.findById(studentId)
                .map(foundStudent -> {
                    //Validari si Verificari

                    /** update kid#1 with friendtoken*/
                    if (newStudent.getFriendToken() != null
                            && newStudent.getFriendToken().length() > 0
                            && !foundStudent.getFriendToken().equals(newStudent.getFriendToken())
                            && !foundStudent.getMyToken().equals(newStudent.getFriendToken())) {
                        foundStudent.setFriendToken(newStudent.getFriendToken());
                    }

                    return studentRepository.save(foundStudent);
                })
                .orElseThrow(() -> new IllegalStateException("student with id " + studentId + " does not exist"));
    }

    /*  ~~~~~~~~~~~ Clear FriendToken ~~~~~~~~~~~ */
    public void clearFriendToken(Long studentId, Student selectedStudent) {
        studentRepository.findById(studentId)
                .map(foundStudent -> {
                    //Validari si Verificari

                    /** clear friendToken and replace with null*/
                    if (!foundStudent.getFriendToken().equals("null")) {
                        foundStudent.setFriendToken(selectedStudent.getFriendToken());
                    }

                    return studentRepository.save(foundStudent);
                })
                .orElseThrow(() -> new IllegalStateException("student with id " + studentId + " does not exist"));
    }

    /* ~~~~~~~~~~~ Validate if friend token exists in db ~~~~~~~~~~~ */
    public String validateFriendToken(StudentDto selectedStudent) {
        if (studentRepository.validateFriendTokenExists(selectedStudent.getFriendToken())) {
            return "All good!";
        }
        return "Friend Token doesn't exist in database!";
    }

    /* ~~~~~~~~~~~ Get second Student knowing his token ~~~~~~~~~~~ */
    public Optional<Student> findStudentByMyToken(String hisToken) {
        return studentRepository.getStudentByMyToken(hisToken);
    }

    /*  ~~~~~~~~~~~ Update Student Camin ~~~~~~~~~~~ */
    public void updateCamin(Long studentId, StudentDto newStudent) {
        studentRepository.findById(studentId)
                .map(foundStudent -> {
                    //Validari si Verificari

                    /** update nume*/
                    if (newStudent.getCamin_preferat() != null
                            && newStudent.getCamin_preferat().length() > 0
                            && !foundStudent.getCamin_preferat().equals(newStudent.getCamin_preferat())) {
                        foundStudent.setCamin_preferat(newStudent.getCamin_preferat());
                    }

                    return studentRepository.save(foundStudent);
                })
                .orElseThrow(() -> new IllegalStateException("student with id " + studentId + " does not exist"));
    }

    /*  ~~~~~~~~~~~ Clear Camin ~~~~~~~~~~~ */
    public void clearCamin(Long studentId, Student selectedStudent) {
        studentRepository.findById(studentId)
                .map(foundStudent -> {
                    //Validari si Verificari

                    /** clear camin and replace with null*/
                    if (!foundStudent.getCamin_preferat().equals("null")) {
                        foundStudent.setCamin_preferat(selectedStudent.getCamin_preferat());
                    }

                    return studentRepository.save(foundStudent);
                })
                .orElseThrow(() -> new IllegalStateException("student with id " + studentId + " does not exist"));
    }

    /*  ~~~~~~~~~~~ Update Flag from Nu to Da and reverse ~~~~~~~~~~~ */
    public void updateFlag(Long studentId) {
        studentRepository.findById(studentId)
                .map(foundStudent -> {
                    //Validari si Verificari

                    /** update flag */
                    foundStudent.setFlagCazSpecial("Nu".equals(foundStudent.getFlagCazSpecial()) ? "Da" : "Nu");

                    return studentRepository.save(foundStudent);
                })
                .orElseThrow(() -> new IllegalStateException("student with id " + studentId + " does not exist"));
    }

}