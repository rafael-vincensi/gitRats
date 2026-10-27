package com.rafaelvincensi.gitrats.user;

import com.rafaelvincensi.gitrats.user.dto.CreateUserRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.LinkGithubRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.UpdateProfileRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request){
        User user = userService.createUser(request);
        var response = UserResponseDTO.fromEntity(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public UserResponseDTO findById(@PathVariable UUID id){
        User user = userService.findById(id);

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getGithubUsername()
        );
    }

    @GetMapping("/username/{username}")
    public UserResponse findByUsername(@PathVariable String username){
        User user = userService.findByUsername(username);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getGithubUsername()
        );
    }

    @PatchMapping("/{id}")
    public UserResponse updateProfileUser(@PathVariable UUID id, @RequestBody UpdateProfileRequestDTO request){
        User user =  userService.updateProfileUser(id, request);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getGithubUsername()
        );
    }

    @PostMapping("/{id}/github")
    public UserResponse linkGithub(@PathVariable UUID id, @RequestBody LinkGithubRequestDTO request){
        User user = userService.linkGithub(id, request);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getGithubUsername()
        );
    }

    @DeleteMapping("/{id}/github")
    public void unlinkGithub(@PathVariable UUID id){
        userService.unlinkGithub(id);
    }
}
