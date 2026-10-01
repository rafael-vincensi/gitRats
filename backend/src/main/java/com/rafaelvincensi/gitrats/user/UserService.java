package com.rafaelvincensi.gitrats.user;

import com.rafaelvincensi.gitrats.common.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserRequest request){

        userRepository.findByUsername(request.username())
                .ifPresent(user -> {
                            throw new BusinessException("Username already exists!");
                });

        userRepository.findByEmail(request.email())
                        .ifPresent(user -> {
                            throw new BusinessException("Email already exists!");
                        });

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordHash);
        user.setCreatedAt(Instant.now());

        return userRepository.save(user);
    }

    public User findById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found!"));
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not Found!"));
}
}
