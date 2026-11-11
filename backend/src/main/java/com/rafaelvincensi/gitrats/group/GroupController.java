package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.GroupResponseDTO;
import com.rafaelvincensi.gitrats.group.dto.UpdateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.UserGroupResponseDTO;
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
    public ResponseEntity<Void> joinGroup(@PathVariable String code, @AuthenticationPrincipal User user){
        GroupMembership joinGroup = groupService.joinGroup(code, user);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupResponseDTO> updateGroup(@PathVariable UUID groupId,
                                                        @Valid @RequestPart("data") UpdateGroupDTO request,
                                                        @AuthenticationPrincipal User user,
                                                        @RequestPart(name = "file", required = false) MultipartFile file){
        Group updateGroup = groupService.updateGroup(groupId, user, request, file);
        var response = GroupResponseDTO.fromEntity(updateGroup);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/exit")
    public ResponseEntity<Void> exitGroup(@PathVariable UUID groupId, @AuthenticationPrincipal User user){
        groupService.exitGroup(user, groupId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserGroupResponseDTO>> listMyGroups(@AuthenticationPrincipal User user){
        var groups = groupService.listMyGroups(user);

        return ResponseEntity.ok(groups);
    }
}