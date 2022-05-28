package com.example.licentaBackendSB.converters;

import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.services.CameraService;
import com.example.licentaBackendSB.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentConverter {

    private final StringUtils stringUtils;
    private final CameraService cameraService;

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
                .friends(student.getFriends() != null ? student.getFriends().stream()
                        .map(friend -> stringUtils.concatenateStrings(friend.getNume(), friend.getPrenume()))
                        .toList() : null)
                .camerePreferate(cameraService.getAllCamerePreferredByStudent(student.getId()).stream()
                        .map(camera -> camera.getNumarCamera() + ", " + stringUtils.mapIntegerNumarPersoaneCameraToString(camera.getNumarTotalPersoane()))
                        .toList())
                .prioritate(student.getPrioritate())
                .cameraRepartizata(student.getCameraRepartizata() != null ? student.getCameraRepartizata().getNumarCamera() : "")
                .camineNedorite(student.getMUndesiredAccommodation().stream().map(Camin::getNumeCamin).toList())
                .caminRepartizat(student.getCameraRepartizata() != null ? student.getCameraRepartizata().getCamin().getNumeCamin() : "")
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
