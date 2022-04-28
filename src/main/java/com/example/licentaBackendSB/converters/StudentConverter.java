package com.example.licentaBackendSB.converters;

import com.example.licentaBackendSB.model.dtos.StudentAplicantDto;
import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.model.entities.StudentAplicant;
import org.springframework.stereotype.Component;

@Component
public class StudentConverter {

    public StudentDto mapStudentEntityToDto(Student student) {
        return StudentDto.builder()
                .an(student.getAn())
                .anUniversitar(String.valueOf(student.getAnUniversitar()))
                .cnp(student.getCnp())
                .camin_preferat(student.getCamin_preferat())
                .flagCazSpecial(student.getFlagCazSpecial())
                .friendToken(student.getFriendToken())
                .genSexual(student.getGenSexual())
                .id(student.getId())
                .grupa(student.getGrupa())
                .judet(student.getJudet())
                .medie(student.getMedie())
                .myToken(student.getMyToken())
                .nume(student.getNume())
                .prenume(student.getPrenume())
                .serie(student.getSerie())
                .zi_de_nastere(student.getZi_de_nastere())
                .master(student.getMaster())
                .isMasterand(student.getIsMasterand())
                .numarLocuriCamera(student.getNumarLocuriCamera())
                .build();
    }

    public Student mapStudentDtoToEntity(StudentDto studentDto) {
        return Student.builder()
                .an(studentDto.getAn())
                .anUniversitar(Integer.parseInt(studentDto.getAnUniversitar()))
                .cnp(studentDto.getCnp())
                .camin_preferat(studentDto.getCamin_preferat())
                .flagCazSpecial(studentDto.getFlagCazSpecial())
                .friendToken(studentDto.getFriendToken())
                .genSexual(studentDto.getGenSexual())
                .id(studentDto.getId())
                .grupa(studentDto.getGrupa())
                .judet(studentDto.getJudet())
                .medie(studentDto.getMedie())
                .myToken(studentDto.getMyToken())
                .nume(studentDto.getNume())
                .prenume(studentDto.getPrenume())
                .serie(studentDto.getSerie())
                .zi_de_nastere(studentDto.getZi_de_nastere())
                .master(studentDto.getMaster())
                .isMasterand(studentDto.getIsMasterand())
                .build();
    }

    public StudentAplicant convertStudentToStudentCamin(Student student, Camin camin) {
        return StudentAplicant.builder()
                .an(student.getAn())
                .cnp(student.getCnp())
                .friendToken(student.getFriendToken())
                .myToken(student.getMyToken())
                .medie(student.getMedie())
                .nume(student.getNume())
                .prenume(student.getPrenume())
                .camin(camin)
                .anUniversitar(student.getAnUniversitar())
                .isCazat(Boolean.FALSE)
                .build();
    }

    public StudentAplicantDto mapStudentCaminEntityToDto(StudentAplicant studentAplicant) {
        return StudentAplicantDto.builder()
                .an(studentAplicant.getAn())
                .anUniversitar(studentAplicant.getAnUniversitar())
                .cnp(studentAplicant.getCnp())
                .id(studentAplicant.getId())
                .friendToken(studentAplicant.getFriendToken())
                .camin(studentAplicant.getCamin())
                .myToken(studentAplicant.getMyToken())
                .nume(studentAplicant.getNume())
                .prenume(studentAplicant.getPrenume())
                .medie(studentAplicant.getMedie())
                .isCazat(studentAplicant.getIsCazat())
                .build();
    }

}
