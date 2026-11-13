package com.rafaelvincensi.gitrats.user;

import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.user.dto.CreateUserRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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
}
