package com.rafaelvincensi.gitrats.CheckIn;

import com.rafaelvincensi.gitrats.CheckIn.dto.CheckInResponseDTO;
import com.rafaelvincensi.gitrats.CheckIn.dto.CreateCheckInDTO;
import com.rafaelvincensi.gitrats.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/checkin")
public class CheckInController {

    private CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public ResponseEntity<CheckInResponseDTO> createCheckIn(@Valid @RequestBody CreateCheckInDTO request, @AuthenticationPrincipal User user){
        CheckIn savedCheckin = checkInService.createCheckIn(user, request);
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
}
