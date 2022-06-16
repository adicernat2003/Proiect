package com.example.licentaBackendSB.converters;

import com.example.licentaBackendSB.model.dtos.StudentDto;
import com.example.licentaBackendSB.model.entities.Camin;
import com.example.licentaBackendSB.model.entities.Student;
import com.example.licentaBackendSB.repositories.CaminRepository;
import com.example.licentaBackendSB.services.CameraService;
import com.example.licentaBackendSB.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentConverter {

    private final StringUtils stringUtils;
    private final CameraService cameraService;
    private final CaminRepository caminRepository;

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
                .zi_de_nastere(student.getZi_de_nastere().replace(".", " "))
                .master(student.getMaster())
                .isMasterand(student.getIsMasterand())
                .friends(student.getFriends() != null ? student.getFriends().stream()
                        .map(friend -> stringUtils.concatenateStrings(friend.getNume(), friend.getPrenume()))
                        .toList() : null)
                .camerePreferate(cameraService.getAllCamerePreferredByStudent(student.getId()).stream()
                        .map(camera -> camera.getNumarCamera() + ", " +
                                stringUtils.mapIntegerNumarPersoaneCameraToString(camera.getNumarTotalPersoane()))
                        .toList())
                .prioritate(student.getPrioritate())
                .cameraRepartizata(student.getCameraRepartizata() != null ?
                        student.getCameraRepartizata().getNumarCamera() : "")
                .camineNedorite(caminRepository.getAllUndesiredCamineOfStudent(student.getId()).stream().map(Camin::getNumeCamin).toList())
                .caminRepartizat(student.getCameraRepartizata() != null ?
                        student.getCameraRepartizata().getCamin().getNumeCamin() : "")
                .build();
    }
}
