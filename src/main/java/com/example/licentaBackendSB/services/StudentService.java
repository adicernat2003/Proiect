package com.example.licentaBackendSB.services;

import com.example.licentaBackendSB.converters.StudentConverter;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.repositories.CaminRepository;
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
    private final CaminRepository caminRepository;

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

    /*  ~~~~~~~~~~~ Update (THIS) with FriendToken ~~~~~~~~~~~ */
    public void updateFriendToken(Long studentId, StudentDto newStudent) {
        Optional<Student> foundStudentOptional = studentRepository.findById(studentId);
        if (foundStudentOptional.isPresent()) {
            Student foundStudent = foundStudentOptional.get();
            if (newStudent.getFriendToken() != null
                    && newStudent.getFriendToken().length() > 0
                    && !newStudent.getFriendToken().equals(foundStudent.getFriendToken())
                    && !newStudent.getFriendToken().equals(foundStudent.getMyToken())) {
                foundStudent.setFriendToken(newStudent.getFriendToken());
                studentRepository.save(foundStudent);
            }
        } else {
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
    }

    /*  ~~~~~~~~~~~ Clear FriendToken ~~~~~~~~~~~ */
    public void clearFriendToken(Long studentId, Student selectedStudent) {
        Optional<Student> foundStudentOptional = studentRepository.findById(studentId);
        if (foundStudentOptional.isPresent()) {
            Student foundStudent = foundStudentOptional.get();
            if (foundStudent.getFriendToken() != null) {
                foundStudent.setFriendToken(selectedStudent.getFriendToken());
                studentRepository.save(foundStudent);
            }
        } else {
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
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
    public void updateCamin(Long studentId, StudentDto newStudent, String anUniversitar) {
        Optional<Student> foundStudentOptional = studentRepository.findById(studentId);
        if (foundStudentOptional.isPresent()) {
            Student foundStudent = foundStudentOptional.get();
            if (newStudent.getCamin_preferat() != null
                    && newStudent.getCamin_preferat().length() > 0
                    && !newStudent.getCamin_preferat().equals(foundStudent.getCamin_preferat())) {
                Optional<Camin> caminOptional = caminRepository.findCaminByNumeCaminAndAnUniversitar(newStudent.getCamin_preferat(), Integer.parseInt(anUniversitar));
                if (caminOptional.isPresent()) {
                    Camin camin = caminOptional.get();
                    foundStudent.setCamin_preferat(camin.getNumeCamin());
                    foundStudent.setCamin(camin);
                    studentRepository.save(foundStudent);
                }
            }
        } else {
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
    }

    /*  ~~~~~~~~~~~ Clear Camin ~~~~~~~~~~~ */
    public void clearCamin(Student student) {
        student.setCamin_preferat(null);
        student.setCamin(null);
        studentRepository.save(student);
    }

    /*  ~~~~~~~~~~~ Update Flag from Nu to Da and reverse ~~~~~~~~~~~ */
    public void updateFlag(Long studentId) {
        Optional<Student> foundStudentOptional = studentRepository.findById(studentId);
        if (foundStudentOptional.isPresent()) {
            Student foundStudent = foundStudentOptional.get();
            foundStudent.setFlagCazSpecial(Boolean.FALSE.equals(foundStudent.getFlagCazSpecial()) ? Boolean.TRUE : Boolean.FALSE);
        } else {
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
    }
}