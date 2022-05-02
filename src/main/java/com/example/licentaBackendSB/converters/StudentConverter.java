package com.example.licentaBackendSB.converters;

import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Camera;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentConverter {

    private final StringUtils stringUtils;

    public StudentDto mapStudentEntityToDto(Student student) {
        return StudentDto.builder()
                .an(student.getAn())
                .anUniversitar(String.valueOf(student.getAnUniversitar()))
                .cnp(student.getCnp())
                .isCazSpecial(student.getIsCazSpecial())
                .genSexual(student.getGenSexual())
                .id(student.getId())
                .grupa(student.getGrupa())
                .judet(student.getJudet())
                .medie(student.getMedie())
                .nume(student.getNume())
                .prenume(student.getPrenume())
                .serie(student.getSerie())
                .zi_de_nastere(student.getZi_de_nastere())
                .master(student.getMaster())
                .isMasterand(student.getIsMasterand())
                .numarLocuriCamera(stringUtils.mapListOfIntegersNumarPersoaneCameraToListOfString(student.getNumarLocuriCamera()))
                .friends(student.getFriends() != null ? student.getFriends().stream()
                        .map(friend -> friend.getNume() + " " + friend.getPrenume())
                        .toList() : null)
                .caminePreferate(student.getCaminePreferate() != null ? student.getCaminePreferate().stream()
                        .map(Camin::getNumeCamin)
                        .toList() : null)
                .camerePreferate(student.getCamerePreferate() != null ? student.getCamerePreferate().stream()
                        .map(Camera::getNumarCamera)
                        .toList() : null)
                .build();
    }

    public Student mapStudentDtoToEntity(StudentDto studentDto) {
        return Student.builder()
                .an(studentDto.getAn())
                .anUniversitar(Integer.parseInt(studentDto.getAnUniversitar()))
                .cnp(studentDto.getCnp())
                .isCazSpecial(studentDto.getIsCazSpecial())
                .genSexual(studentDto.getGenSexual())
                .id(studentDto.getId())
                .grupa(studentDto.getGrupa())
                .judet(studentDto.getJudet())
                .medie(studentDto.getMedie())
                .nume(studentDto.getNume())
                .prenume(studentDto.getPrenume())
                .serie(studentDto.getSerie())
                .zi_de_nastere(studentDto.getZi_de_nastere())
                .master(studentDto.getMaster())
                .isMasterand(studentDto.getIsMasterand())
                .build();
    }

}
