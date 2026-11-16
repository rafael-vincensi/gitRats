package com.rafaelvincensi.gitrats.auth;

import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.swing.*;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should load user by username successfully")
    void shouldLoadUserByUsernameSuccessfully() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("rafinhacracked");
        user.setEmail("rafinhacracked@gmail.com");

        when(userRepository.findUserDetailsByEmail(user.getEmail())).thenReturn(user);

        UserDetails result = authService.loadUserByUsername("rafinhacracked@gmail.com");

        assertNotNull(result);
        assertEquals("rafinhacracked", result.getUsername());
        assertEquals("rafinhacracked@gmail.com", ((User) result).getEmail());
        verify(userRepository, times(1)).findUserDetailsByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound(){

        String email = ("sirbaudelaire@gmail.com");

        when(userRepository.findUserDetailsByEmail(email)).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.loadUserByUsername(email)
        );

        assertEquals("User not found with email " + email, exception.getMessage());
        verify(userRepository, times(1)).findUserDetailsByEmail(email);
    }
}
