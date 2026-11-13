package com.rafaelvincensi.gitrats.user;

import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.exception.ConflictException;
import com.rafaelvincensi.gitrats.exception.FileUploadException;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.user.dto.CreateUserRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.LinkGithubRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.UpdateProfileRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UploadService uploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
    }

    public User createUser(CreateUserRequestDTO request){
        userRepository.findByUsername(request.username())
                .ifPresent(user -> {
                            throw new ConflictException("Username already exists!");
                });

        userRepository.findByEmail(request.email())
                        .ifPresent(user -> {
                            throw new ConflictException("Email already exists!");
                        });

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordHash);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User findById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found!"));
}

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }

    public User findByGithubId(String githubId){
        return userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }

    public User updateProfileUser(UUID id, UpdateProfileRequestDTO request, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (request.username() != null && !request.username().isBlank()){
            userRepository.findByUsername(request.username())
                    .ifPresent(existingUser -> {
                        if (!user.getId().equals(existingUser.getId())) {
                            throw new ConflictException("User already exists!");
                        }
                    });
            user.setUsername(request.username());
           }

        if (file != null && !file.isEmpty()) {
            try {
                byte[] imageBytes = file.getBytes();
                String newAvatar = uploadService.uploadFile(imageBytes, "avatars");
                user.setAvatarUrl(newAvatar);
            } catch (IOException e) {
                throw new FileUploadException("Failed to read image bytes: " + e.getMessage());
            }
        }

        if (request.name() != null && !request.name().isBlank()) user.setName(request.name());
        if (request.bio() != null) user.setBio(request.bio());
        if (request.timezone() != null) user.setTimezone(request.timezone());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User linkGithub(UUID id, LinkGithubRequestDTO request){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ConflictException("User not found!"));

        Optional<User> useGithubId = userRepository.findByGithubId(request.githubId());
            if (useGithubId.isPresent()){
                if (!user.getId().equals(useGithubId.get().getId()))
                    throw new ConflictException("GitHub is linked to another user!");
                 }
                user.setGithubId(request.githubId());
                user.setGithubUsername(request.githubUsername());
                return userRepository.save(user);
            }

        public User unlinkGithub(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        user.setGithubId(null);
        user.setGithubUsername(null);

        return userRepository.save(user);
        }
    }
