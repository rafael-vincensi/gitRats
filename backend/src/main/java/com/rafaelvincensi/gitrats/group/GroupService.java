package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    public GroupService(GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository) {
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
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

}
