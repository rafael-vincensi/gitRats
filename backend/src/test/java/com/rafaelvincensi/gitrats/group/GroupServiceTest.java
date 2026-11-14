package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
