package com.rafaelvincensi.gitrats.checkIn;

import com.rafaelvincensi.gitrats.checkIn.dto.CheckInResponseDTO;
import com.rafaelvincensi.gitrats.checkIn.dto.CreateCheckInDTO;
import com.rafaelvincensi.gitrats.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/checkin")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CheckInResponseDTO> createCheckIn(@AuthenticationPrincipal User user,
                                                            @Valid @RequestPart("data") CreateCheckInDTO request,
                                                            @RequestPart("file")MultipartFile file){
        CheckIn savedCheckin = checkInService.createCheckIn(user, request, file);
        var response = CheckInResponseDTO.fromEntity(savedCheckin);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<CheckInResponseDTO>> checkInsByGroup(@PathVariable UUID groupId){
        List<CheckIn> checkInListByGroup = checkInService.checkInsByGroup(groupId);
        var response = checkInListByGroup.stream()
                .map(checkIn -> CheckInResponseDTO.fromEntity(checkIn))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<CheckInResponseDTO>> allCheckIns(@AuthenticationPrincipal User user){
        List<CheckIn> checkInList = checkInService.allCheckIns(user);
        var response = checkInList.stream()
                .map(checkIn -> CheckInResponseDTO.fromEntity(checkIn))
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCheckIn(@AuthenticationPrincipal User user, @PathVariable UUID id){
        checkInService.deleteCheckIn(user, id);

        return ResponseEntity.noContent().build();
    }
}
