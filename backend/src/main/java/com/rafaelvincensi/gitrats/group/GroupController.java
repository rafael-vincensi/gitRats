package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.GroupResponseDTO;
import com.rafaelvincensi.gitrats.group.dto.UpdateGroupDTO;
import com.rafaelvincensi.gitrats.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PostMapping("/{code}/join")
    public ResponseEntity<Void> joinGroup(@PathVariable String code,
                                                      @AuthenticationPrincipal User user){
        GroupMembership joinGroup = groupService.joinGroup(code, user);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponseDTO> updateGroup(@PathVariable UUID groupId,
                                                        @Valid @RequestBody UpdateGroupDTO request,
                                                        @AuthenticationPrincipal User user){
        Group updateGroup = groupService.updateGroup(groupId, user, request);
        var response = GroupResponseDTO.fromEntity(updateGroup);

        return ResponseEntity.ok(response);
    }
}
