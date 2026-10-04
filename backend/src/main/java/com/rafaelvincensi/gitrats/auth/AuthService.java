package com.rafaelvincensi.gitrats.auth;

import com.rafaelvincensi.gitrats.common.exception.BusinessException;
import com.rafaelvincensi.gitrats.token.TokenProvider;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse login(LoginRequest request){
        User user = userService.findByEmail(request.email());

        if (passwordEncoder.matches(request.password(), user.getPasswordHash())){
            String token = tokenProvider.generateToken(user);
                return new LoginResponse(token);
        }
        throw new BusinessException("Invalid email or password!");
    }
}
