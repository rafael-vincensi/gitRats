package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.exception.ConflictException;
import com.rafaelvincensi.gitrats.exception.GroupMaxCapacityException;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.UpdateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.UserGroupResponseDTO;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final UserService userService;

    public GroupService(GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.userService = userService;
    }

    @Transactional
    public Group createGroup(CreateGroupDTO request, User userFounder){

        String invitationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Group group = new Group();
        group.setName(request.name());
        group.setDescription(request.description());
        group.setCode(invitationCode);
        group.setCreatedAt(LocalDateTime.now());

        Group savedGroup = groupRepository.save(group);

        GroupMembership membership = new GroupMembership();

        membership.setUser(userFounder);
        membership.setGroup(savedGroup);
        membership.setRole(GroupRole.FOUNDER);

        groupMembershipRepository.save(membership);

        return savedGroup;
    }

    public GroupMembership joinGroup(String code, User user){

        Group group = groupRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Group not found!"));

        boolean inGroup = groupMembershipRepository.existsByGroupIdAndUserId(group.getId(), user.getId());

        if (inGroup){
            throw new ConflictException("You already a member of this group!");
        }

        long groupMembersCount = groupMembershipRepository.countByGroupId(group.getId());

        if (groupMembersCount >= 20){
            throw new GroupMaxCapacityException("Group already has 20 members!");
        }

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setRole(GroupRole.MEMBER);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());

        return groupMembershipRepository.save(membership);
    }

    public Group updateGroup(UUID groupId, User user, UpdateGroupDTO request){
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found!"));

        boolean isFounder = groupMembershipRepository.existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER);

        if (!isFounder) {
            throw new AccessDeniedException("Only the group founder can perform this action!");
        }

        if (group.getName() != null && !request.name().isBlank()) group.setName(request.name());
        if (group.getDescription() != null && !request.description().isBlank()) group.setDescription(request.description());
        if (group.getAvatarUrl() != null && !request.avatarUrl().isBlank()) group.setAvatarUrl(request.avatarUrl());

        return groupRepository.save(group);
    }

    public void exitGroup(User user, UUID groupId){
        var membership = groupMembershipRepository.findByGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new NotFoundException("You are not a member of this group!"));

        if (membership.getRole() == GroupRole.FOUNDER){
            throw new AccessDeniedException("The founder cannot exit the group! Delete group instead.");
        }

        groupMembershipRepository.delete(membership);
    }

    public List<UserGroupResponseDTO> listMyGroups(User user){
        var memberships = groupMembershipRepository.findByUserId(user.getId());

        return memberships.stream()
                .map(membership -> {
                    Long totalMembers = groupMembershipRepository.countByGroupId(membership.getGroup().getId());

                    return UserGroupResponseDTO.fromEntity(membership, totalMembers);
                })
                .toList();
    }
}
