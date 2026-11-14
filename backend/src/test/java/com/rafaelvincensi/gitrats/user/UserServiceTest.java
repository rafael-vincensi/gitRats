package com.rafaelvincensi.gitrats.user;

import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.exception.ConflictException;
import com.rafaelvincensi.gitrats.exception.FileUploadException;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.user.dto.CreateUserRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.UpdateProfileRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully(){
        CreateUserRequestDTO request = new CreateUserRequestDTO("rafinhagoat", "rafael", "rafael@gmail.com", "rafinhagoatsenha");

        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createUser(request);

        assertNotNull(createdUser);
        assertEquals("rafinhagoat", createdUser.getUsername());
        assertEquals("rafael@gmail.com", createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPasswordHash());
        assertNotNull(createdUser.getCreatedAt());

        verify(passwordEncoder, times(1)).encode("rafinhagoatsenha");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when username already exists")
    void shouldThrowConflictExceptionWhenUsernameAlreadyExists(){
        CreateUserRequestDTO request = new CreateUserRequestDTO("rafinhagoat", "rafael", "rafael@gmail.com", "rafinhagoatsenha");

        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(new User()));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Username already exists!", exception.getMessage());

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when email already exists")
    void shouldThrowConflictExceptionWhenEmailAlreadyExists(){
        CreateUserRequestDTO request = new CreateUserRequestDTO("rafinhagoat", "rafael", "rafael@gmail.com", "rafinhagoatsenha");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new User()));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Email already exists!", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(request.email());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find user by id successfully")
    void shouldFindUserByIdSuccessfully(){
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("rafinhagoat");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User foundUser = userService.findById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals("rafinhagoat", foundUser.getUsername());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when user id not found")
    void shouldThrowNotFoundExceptionWhenUserNotFound(){
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.findById(userId)
        );

        assertEquals("User not found!", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should find user by username successfully")
    void shouldFindUserByUsernameSuccessfully(){
        User user = new User();
        user.setUsername("rafinhagoat");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername(user.getUsername());

        assertNotNull(foundUser);
        assertEquals("rafinhagoat", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    @DisplayName("Should throw NotFoundException when user username not found")
    void shouldThrowNotFoundExceptionWhenUsernameNotFound(){
        String username = "rafinhainexistente";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.findByUsername(username)
        );

        assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should find user by githubId successfully")
    void shouldFindUserByGithubIdSuccessfully(){
        User user = new User();
        user.setGithubId("583231");

        when(userRepository.findByGithubId(user.getGithubId())).thenReturn(Optional.of(user));

        User foundUser = userService.findByGithubId(user.getGithubId());

        assertNotNull(foundUser);
        assertEquals("583231", foundUser.getGithubId());
        verify(userRepository, times(1)).findByGithubId(user.getGithubId());
    }

    @Test
    @DisplayName("Should throw NotFoundException when user GithubId not found")
    void shouldThrowNotFoundExceptionWhenGithubIdNotFound(){
        String githubId = "583232";

        when(userRepository.findByGithubId(githubId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.findByGithubId(githubId)
        );

        assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findByGithubId(githubId);
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void shouldUpdateUserProfileSuccessfully(){
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("rafinhagoat");
        existingUser.setName("rafinha GOAT");

        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                "ye",
                "rafinha GOAT",
                "bio fera demais",
                "America/Sao_Paulo");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updateUser = userService.updateProfileUser(userId, request, null);

        assertNotNull(updateUser);
        assertEquals("ye", updateUser.getUsername());
        assertEquals("rafinha GOAT", updateUser.getName());
        assertEquals("bio fera demais", updateUser.getBio());
        assertEquals("America/Sao_Paulo", updateUser.getTimezone());
        assertNotNull(updateUser.getUpdatedAt());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByUsername(request.username());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when username already exists")
    void shouldThrowConflictExceptionAlreadyExists(){
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setUsername("rafinhagoat");

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("ye");

        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                "ye",
                "rafinha GOAT",
                "im CEO bitch",
                null);

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(otherUser.getUsername())).thenReturn(Optional.of(otherUser));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.updateProfileUser(existingUser.getId(), request, null)
        );

        assertEquals("Username already exists!", exception.getMessage());
        verify(userRepository, times(1)).findById(existingUser.getId());
        verify(userRepository, times(1)).findByUsername("ye");
    }

    @Test
    @DisplayName("Should throw NotFoundException when user not found")
    void shouldThrowNotFoundExceptionWhenUpdateUserNotFound(){
        UUID userId = UUID.randomUUID();

        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(null, null, "bio user not found", null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateProfileUser(userId, request, null)
        );

        assertEquals("User not found!", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should update user profile with avatar successfully")
    void shouldUpdateUserProfileWithAvatarSuccessfully(){
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("rafinhagoat");

        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                null,
                "rafaeu",
                "cs",
                null
        );

        MockMultipartFile avatar = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpg",
                "fake-image-bytes".getBytes());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(uploadService.uploadFile(any(byte[].class), eq("avatars"))).thenReturn("https://cloudinary.com/image.jpg");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateProfileUser(user.getId(), request, avatar);

        assertNotNull(updatedUser);
        assertEquals("https://cloudinary.com/image.jpg", updatedUser.getAvatarUrl());
        assertNotNull(updatedUser.getUpdatedAt());

        verify(userRepository, times(1)).findById(user.getId());
        verify(uploadService, times(1)).uploadFile(any(byte[].class), eq("avatars"));
        verify(userRepository, times(1)).save(any(User.class));
    }
}
