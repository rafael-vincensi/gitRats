package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.exception.ConflictException;
import com.rafaelvincensi.gitrats.exception.GroupMaxCapacityException;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.UpdateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.UserGroupResponseDTO;
import com.rafaelvincensi.gitrats.rank.LeaderboardService;
import com.rafaelvincensi.gitrats.rank.dto.GroupLeaderboardDTO;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

import javax.swing.*;
import java.util.List;
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

    @Mock
    private UserService userService;

    @Mock
    private LeaderboardService leaderboardService;

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

    @Test
    @DisplayName("Should update group successfully")
    void shouldUpdateGroupSuccessfully(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        UpdateGroupDTO request = new UpdateGroupDTO("codas fofos", "so pra gurizada do python");

        MockMultipartFile file = new MockMultipartFile(
                "group-image",
                "groupimage.jpg",
                "image/jpg",
                "fake-image-bytes".getBytes());

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER)).thenReturn(true);
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(uploadService.uploadFile(any(byte[].class), eq("groups"))).thenReturn("https://cloudinary.com/image.jpg");


        Group updatedGroup = groupService.updateGroup(group.getId(), user, request, file);

        assertNotNull(updatedGroup);
        assertEquals("codas fofos", updatedGroup.getName());
        assertEquals("so pra gurizada do python", group.getDescription());
        assertEquals("https://cloudinary.com/image.jpg", group.getAvatarUrl());

        verify(groupRepository, times(1)).findById(group.getId());
        verify(groupMembershipRepository, times(1)).existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER);
        verify(uploadService, times(1)).uploadFile(any(byte[].class), eq("groups"));
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when group not found to update")
    void shouldThrowNotFoundExceptionWhenGroupNotFoundToUpdate(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        UpdateGroupDTO request = new UpdateGroupDTO("ye", "ye");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> groupService.updateGroup(group.getId(), user, request, null)
        );

        assertEquals("Group not found!", exception.getMessage());

        verify(groupRepository, times(1)).findById(group.getId());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user not is the founder to update")
    void shouldThrowAccessDeniedExceptionWhenUserNotIsTheFounderToUpdate() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        UpdateGroupDTO request = new UpdateGroupDTO("ratificar cachorro peti bul", "e crianca");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER)).thenReturn(false);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> groupService.updateGroup(group.getId(), user, request, null)
        );

        assertEquals("Only the group founder can perform this action!", exception.getMessage());

        verify(groupRepository, times(1)).findById(group.getId());
        verify(groupMembershipRepository, times(1)).existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER);
    }

    @Test
    @DisplayName("Should exit group successfully")
    void shouldExitGroupSuccessfully(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setRole(GroupRole.MEMBER);

        when(groupMembershipRepository.findByGroupIdAndUserId(group.getId(), user.getId())).thenReturn(Optional.of(membership));

        groupService.exitGroup(user, group.getId());

        assertNotNull(group);
        verify(groupMembershipRepository, times(1)).findByGroupIdAndUserId(group.getId(), user.getId());
        verify(groupMembershipRepository, times(1)).delete(membership);
    }

    @Test
    @DisplayName("Should throw NotFoundException when a user tries to leave a group they are not a member of")
    void shouldThrowNotFoundExceptionWhenUserTriesToLeaveGroupTheyAreNotIn(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        when(groupMembershipRepository.findByGroupIdAndUserId(group.getId(), user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> groupService.exitGroup(user, group.getId())
        );

        assertEquals("You are not a member of this group!", exception.getMessage());
        verify(groupMembershipRepository, times(1)).findByGroupIdAndUserId(group.getId(), user.getId());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when a user tries to leave a group they are the founder")
    void shouldThrowAccessDeniedExceptionWhenUserTriesToLeaveGroupTheyAreFounder(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setRole(GroupRole.FOUNDER);

        when(groupMembershipRepository.findByGroupIdAndUserId(group.getId(), user.getId())).thenReturn(Optional.of(membership));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> groupService.exitGroup(user, group.getId())
        );

        assertEquals("The founder cannot exit the group! Delete group instead.", exception.getMessage());

        verify(groupMembershipRepository, times(1)).findByGroupIdAndUserId(group.getId(), user.getId());
    }

    @Test
    @DisplayName("Should list groups successfully")
    void shouldListGroupsSuccessfully() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Group groupOne = new Group();
        groupOne.setId(UUID.randomUUID());
        groupOne.setName("FE!N FE!N FE!N");
        groupOne.setCode("FEIN01");

        Group groupTwo = new Group();
        groupTwo.setId(UUID.randomUUID());
        groupTwo.setName("potato salad");
        groupTwo.setCode("POTATO");

        GroupMembership membershipOne = new GroupMembership();
        membershipOne.setId(UUID.randomUUID());
        membershipOne.setUser(user);
        membershipOne.setGroup(groupOne);
        membershipOne.setRole(GroupRole.FOUNDER);

        GroupMembership membershipTwo = new GroupMembership();
        membershipTwo.setId(UUID.randomUUID());
        membershipTwo.setUser(user);
        membershipTwo.setGroup(groupTwo);
        membershipTwo.setRole(GroupRole.MEMBER);

        when(groupMembershipRepository.findByUserId(user.getId())).thenReturn(List.of(membershipOne, membershipTwo));
        when(groupMembershipRepository.countByGroupId(groupOne.getId())).thenReturn(5L);
        when(groupMembershipRepository.countByGroupId(groupTwo.getId())).thenReturn(12L);
        when(leaderboardService.getGroupCheckInLeaderboard(groupOne.getId())).thenReturn(List.of(
                new GroupLeaderboardDTO(UUID.randomUUID(), "Steve Lacy", "uuuu", null, 5L),
                new GroupLeaderboardDTO(UUID.randomUUID(), "Frank Ocean", "blonded", null, 14L)
        ));
        when(leaderboardService.getGroupCommitLeaderboard(groupOne.getId())).thenReturn(List.of(
                new GroupLeaderboardDTO(UUID.randomUUID(), "ALLRED", "allred", null, 42L),
                new GroupLeaderboardDTO(UUID.randomUUID(), "Petal", "petal", null, 31L)
        ));
        when(leaderboardService.getGroupCheckInLeaderboard(groupTwo.getId())).thenReturn(List.of(
                new GroupLeaderboardDTO(UUID.randomUUID(), "BULLY", "ye", null, 9L),
                new GroupLeaderboardDTO(UUID.randomUUID(), "BOY IS A GUN", "igor", null, 3L)
        ));
        when(leaderboardService.getGroupCommitLeaderboard(groupTwo.getId())).thenReturn(List.of());

        List<UserGroupResponseDTO> result = groupService.listMyGroups(user);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(groupOne.getId(), result.get(0).id());
        assertEquals("FE!N FE!N FE!N", result.get(0).name());
        assertEquals("FEIN01", result.get(0).code());
        assertEquals(GroupRole.FOUNDER, result.get(0).role());
        assertEquals(5L, result.get(0).memberCount());
        assertEquals(19L, result.get(0).totalCheckIns());
        assertEquals(73L, result.get(0).totalCommits());

        assertEquals(groupTwo.getId(), result.get(1).id());
        assertEquals("potato salad", result.get(1).name());
        assertEquals("POTATO", result.get(1).code());
        assertEquals(GroupRole.MEMBER, result.get(1).role());
        assertEquals(12L, result.get(1).memberCount());
        assertEquals(12L, result.get(1).totalCheckIns());
        assertEquals(0L, result.get(1).totalCommits());

        verify(groupMembershipRepository, times(1)).findByUserId(user.getId());
        verify(groupMembershipRepository, times(1)).countByGroupId(groupOne.getId());
        verify(groupMembershipRepository, times(1)).countByGroupId(groupTwo.getId());
        verify(leaderboardService, times(1)).getGroupCheckInLeaderboard(groupOne.getId());
        verify(leaderboardService, times(1)).getGroupCommitLeaderboard(groupOne.getId());
        verify(leaderboardService, times(1)).getGroupCheckInLeaderboard(groupTwo.getId());
        verify(leaderboardService, times(1)).getGroupCommitLeaderboard(groupTwo.getId());
    }

    @Test
    @DisplayName("Should return empty list when user has no groups")
    void shouldReturnEmptyListWhenUserHasNoGroups(){
        User user = new User();
        user.setId(UUID.randomUUID());

        when(groupMembershipRepository.findByUserId(user.getId())).thenReturn(List.of());

        List<UserGroupResponseDTO> result = groupService.listMyGroups(user);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(groupMembershipRepository, times(1)).findByUserId(user.getId());
        verify(groupMembershipRepository, never()).countByGroupId(any());
    }

    @Test
    @DisplayName("Should delete group successfully")
    void shouldDeleteGroupSuccessfully() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        when(userService.findById(user.getId())).thenReturn(user);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER)).thenReturn(true);

        groupService.deleteGroup(user.getId(), group.getId());

        verify(userService, times(1)).findById(user.getId());
        verify(groupRepository, times(1)).findById(group.getId());
        verify(groupMembershipRepository, times(1)).existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER);
        verify(groupMembershipRepository, times(1)).deleteByGroupId(group.getId());
        verify(groupRepository, times(1)).deleteById(group.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException when group not found to delete")
    void shouldThrowNotFoundExceptionWhenGroupNotFoundToDelete(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        when(userService.findById(user.getId())).thenReturn(user);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> groupService.deleteGroup(user.getId(), group.getId())
        );

        assertEquals("Group not found!", exception.getMessage());

        verify(userService, times(1)).findById(user.getId());
        verify(groupRepository, times(1)).findById(group.getId());
        verify(groupRepository, never()).deleteById(any());
        verify(groupMembershipRepository, never()).deleteByGroupId(any());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user is not founder of group to delete")
    void shouldThrowAccessDeniedExceptionWhenUserIsNotFounderToDeleteGroup(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(UUID.randomUUID());

        when(userService.findById(user.getId())).thenReturn(user);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER)).thenReturn(false);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> groupService.deleteGroup(user.getId(), group.getId())
        );

        assertEquals("Only the Founder can delete the group!", exception.getMessage());
        verify(userService, times(1)).findById(user.getId());
        verify(groupRepository, times(1)).findById(group.getId());
        verify(groupMembershipRepository, times(1)).existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER);
        verify(groupRepository, never()).deleteById(any());
        verify(groupMembershipRepository, never()).deleteByGroupId(group.getId());
    }
}
