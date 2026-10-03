package com.rafaelvincensi.gitrats.user;

import com.rafaelvincensi.gitrats.common.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
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

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found!"));
    }

    public User findByGithubId(String githubId){
        return userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new BusinessException("User not found!"));
    }

    public User updateProfileUser(UUID id, UpdateProfileRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found!"));

       if (request.username() != null) {
           if (request.username().isBlank()){
               throw new BusinessException("Username cannot be empty or blank!");
           }

           Optional<User> userOptional = userRepository.findByUsername(request.username());
           if (userOptional.isPresent()){
               if (!user.getId().equals(userOptional.get().getId())){
                   throw new BusinessException("Username already exists!");
               }
           }
       }

        if (request.name() != null) user.setName(request.name());
        if (request.bio() != null) user.setBio(request.bio());
        if (request.avatarUrl() != null) user.setAvatarUrl(request.avatarUrl());
        if (request.timezone() != null) user.setTimezone(request.timezone());

        return userRepository.save(user);
    }

    public User linkGithub(UUID id, LinkGithubRequest request){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found!"));

        Optional<User> useGithubId = userRepository.findByGithubId(request.githubId());
            if (useGithubId.isPresent()){
                if (!user.getId().equals(useGithubId.get().getId()))
                    throw new BusinessException("GitHub is linked to another user!");
                 }
                user.setGithubId(request.githubId());
                user.setGithubUsername(request.githubUsername());
                return userRepository.save(user);
            }

        public User unlinkGithub(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found!"));

        user.setGithubId(null);
        user.setGithubUsername(null);

        return userRepository.save(user);
        }
    }
