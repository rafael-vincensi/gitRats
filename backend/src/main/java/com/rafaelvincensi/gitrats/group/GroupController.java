package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.GroupResponseDTO;
import com.rafaelvincensi.gitrats.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping()
    public ResponseEntity<GroupResponseDTO> createGroup(@Valid @RequestBody CreateGroupDTO request,
                                                        @AuthenticationPrincipal User user){
        Group savedGroup = groupService.createGroup(request, user);
        var response = GroupResponseDTO.fromEntity(savedGroup);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
