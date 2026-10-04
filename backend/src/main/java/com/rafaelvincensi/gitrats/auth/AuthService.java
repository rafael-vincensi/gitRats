package com.rafaelvincensi.gitrats.auth;

import com.rafaelvincensi.gitrats.common.exception.BusinessException;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(LoginRequest request){
        User user = userService.findByEmail(request.email());

        if (passwordEncoder.matches(request.password(), user.getPasswordHash())){
            return user;
        }
        throw new BusinessException("Invalid email or password!");
    }
}
