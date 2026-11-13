package com.rafaelvincensi.gitrats.user;

import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.exception.ConflictException;
import com.rafaelvincensi.gitrats.user.dto.CreateUserRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

}
