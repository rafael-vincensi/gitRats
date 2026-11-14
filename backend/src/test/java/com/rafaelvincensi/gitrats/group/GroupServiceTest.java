package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.exception.ConflictException;
import com.rafaelvincensi.gitrats.exception.GroupMaxCapacityException;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private GroupService groupService;

    @Test
    @DisplayName("Should create group and assign founder membership successfully")
    void shouldCreateGroupSuccessfully() {
        User founder = new User();
        founder.setId(UUID.randomUUID());
        founder.setUsername("juniormelansia");

        CreateGroupDTO request = new CreateGroupDTO(
                "javascript = entrosa",
                "so java, sem script");

        when(groupRepository.save(any(Group.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        when(groupMembershipRepository.save(any(GroupMembership.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        Group createdGroup = groupService.createGroup(request, founder);

        assertNotNull(createdGroup);
        assertEquals("javascript = entrosa", createdGroup.getName());
        assertEquals("so java, sem script", createdGroup.getDescription());
        assertNotNull(createdGroup.getCode());
        assertEquals(6, createdGroup.getCode().length());
        assertNotNull(createdGroup.getCreatedAt());

        verify(groupRepository, times(1)).save(any(Group.class));
        verify(groupMembershipRepository, times(1)).save(any(GroupMembership.class));
    }

    @Test
    @DisplayName("Should join in group successfully")
    void shouldJoinGroupSuccessfully(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setCode("ABC123");

        when(groupRepository.findByCode("ABC123")).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserId(group.getId(), user.getId())).thenReturn(false);
        when(groupMembershipRepository.countByGroupId(group.getId())).thenReturn(5L);
        when(groupMembershipRepository.save(any(GroupMembership.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        GroupMembership joinGroup = groupService.joinGroup("ABC123", user);

        assertNotNull(joinGroup);
        assertEquals(group, joinGroup.getGroup());
        assertEquals(user, joinGroup.getUser());
        assertEquals(GroupRole.MEMBER, joinGroup.getRole());
        assertEquals("ABC123", group.getCode());
        assertNotNull(joinGroup.getJoinedAt());

        verify(groupRepository, times(1)).findByCode("ABC123");
        verify(groupMembershipRepository, times(1)).existsByGroupIdAndUserId(group.getId(), user.getId());
        verify(groupMembershipRepository, times(1)).countByGroupId(group.getId());
        verify(groupMembershipRepository, times(1)).save(any(GroupMembership.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when group not found")
    void shouldThrowNotFoundExceptionWhenGroupNotFound(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setCode("ABC123");

        when(groupRepository.findByCode("ABC123")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> groupService.joinGroup("ABC123", user)
        );

        assertEquals("Group not found!", exception.getMessage());

        verify(groupRepository, times(1)).findByCode("ABC123");
        verify(groupMembershipRepository, never()).save(any(GroupMembership.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when user already is a group member")
    void shouldThrowConflictExceptionWhenUserAlreadyIsAGroupMember(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setCode("ABC123");

        when(groupRepository.findByCode("ABC123")).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserId(group.getId(), user.getId())).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> groupService.joinGroup("ABC123", user)
        );

        assertEquals("You already a member of this group!", exception.getMessage());

        verify(groupRepository, times(1)).findByCode("ABC123");
        verify(groupMembershipRepository, times(1)).existsByGroupIdAndUserId(group.getId(), user.getId());
    }

    @Test
    @DisplayName("Should throw GroupMaxCapacityException when group has 20 members")
    void shouldThrowGroupMaxCapacityExceptionWhenGroupIsFull(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setCode("ABC123");

        when(groupRepository.findByCode("ABC123")).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserId(group.getId(), user.getId())).thenReturn(false);
        when(groupMembershipRepository.countByGroupId(group.getId())).thenReturn(20L);

        GroupMaxCapacityException exception = assertThrows(
                GroupMaxCapacityException.class,
                () -> groupService.joinGroup("ABC123", user)
        );

        assertEquals("Group already has 20 members!", exception.getMessage());

        verify(groupRepository, times(1)).findByCode("ABC123");
        verify(groupMembershipRepository, times(1)).existsByGroupIdAndUserId(group.getId(), user.getId());
        verify(groupMembershipRepository, times(1)).countByGroupId(group.getId());
    }

}
