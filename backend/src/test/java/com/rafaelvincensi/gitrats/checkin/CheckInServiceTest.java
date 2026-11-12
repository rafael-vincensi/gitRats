package com.rafaelvincensi.gitrats.checkin;

import com.rafaelvincensi.gitrats.checkIn.CheckIn;
import com.rafaelvincensi.gitrats.checkIn.CheckInRepository;
import com.rafaelvincensi.gitrats.checkIn.CheckInService;
import com.rafaelvincensi.gitrats.checkIn.dto.CreateCheckInDTO;
import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.exception.FileUploadException;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.group.Group;
import com.rafaelvincensi.gitrats.group.GroupMembershipRepository;
import com.rafaelvincensi.gitrats.group.GroupRepository;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInServiceTest {

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private CheckInService checkInService;

    @Test
    @DisplayName("Should create checkin successfully when data is valid")
    void shouldCreateCheckInSuccessfully() throws IOException {
        UUID groupId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCurrentStreak(0);
        user.setHighestStreak(0);
        user.setTotalCheckIns(0);
        user.setLastCheckIn(null);

        CreateCheckInDTO request = new CreateCheckInDTO("SPRING", "200 commits in gitrats!!!!", groupId);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpg",
                "fake-image-bytes".getBytes()
        );

        Group group = new Group();
        group.setId(groupId);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserId(groupId, user.getId())).thenReturn(true);
        when(uploadService.uploadFile(any(byte[].class), eq("checkins"))).thenReturn("https://cloudinary.com/image.jpg");
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(a -> a.getArgument(0));

        CheckIn result = checkInService.createCheckIn(user, request, file);

        assertNotNull(result);
        assertEquals("SPRING", result.getTitle());
        assertEquals("200 commits in gitrats!!!!", result.getDescription());
        assertEquals("https://cloudinary.com/image.jpg", result.getImageUrl());
        assertEquals(1, user.getCurrentStreak());
        assertEquals(1, user.getTotalCheckIns());
        assertEquals(LocalDate.now(), user.getLastCheckIn());

        verify(userRepository, times(1)).save(user);
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user is not a group member")
    void shouldThrowAccessDeniedExceptionWhenUserIsNotGroupMember(){
        UUID groupId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());

        CreateCheckInDTO request = new CreateCheckInDTO("GO", "200 commits", groupId);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpg",
                "fake-image-bytes".getBytes()
        );

        Group group = new Group();
        group.setId(groupId);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserId(groupId, user.getId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> checkInService.createCheckIn(user, request, file));

        assertEquals("You can only post check-in in your groups!", exception.getMessage());
        verifyNoInteractions(uploadService);
        verify(checkInRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw FileUploadException when file is null or empty")
    void shouldThrowFileUploadExceptionWhenFileIsNullOrEmpty() {
        UUID groupId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());

        Group group = new Group();
        group.setId(groupId);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.existsByGroupIdAndUserId(groupId, user.getId())).thenReturn(true);

        CreateCheckInDTO request = new CreateCheckInDTO("Go", "no image commit", groupId);

        MultipartFile file = null;

        FileUploadException exception = assertThrows(
                FileUploadException.class,
                () -> checkInService.createCheckIn(user, request, file)
        );
        assertEquals("A check-in image is required!", exception.getMessage());

        verifyNoInteractions(uploadService);
        verify(checkInRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when group not found")
    void shouldThrowWhenGroupNotFound(){
        UUID groupId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());

        CreateCheckInDTO request = new CreateCheckInDTO("Rust", "Null Group!", groupId);

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpg",
                "fake-image-bytes".getBytes()
        );

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> checkInService.createCheckIn(user, request, file));

        assertEquals("Group not found!", exception.getMessage());

        verifyNoInteractions(uploadService);
        verify(checkInRepository, never()).save(any());
    }

}
