package com.rafaelvincensi.gitrats.user;

import com.rafaelvincensi.gitrats.github.GithubService;
import com.rafaelvincensi.gitrats.user.dto.CreateUserRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.LinkGithubRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.UpdateProfileRequestDTO;
import com.rafaelvincensi.gitrats.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final GithubService githubService;

    public UserController(UserService userService, GithubService githubService) {
        this.userService = userService;
        this.githubService = githubService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request){
        User user = userService.createUser(request);
        var response = UserResponseDTO.fromEntity(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(@AuthenticationPrincipal User user){
        Integer weeklyCommits = githubService.getWeeklyCommits(user.getGithubUsername(), user.getGithubAccessToken());

        var response = UserResponseDTO.fromEntity(user, weeklyCommits);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id){
        User userById = userService.findById(id);
        var response = UserResponseDTO.fromEntity(userById);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> findByUsername(@PathVariable String username){
        User userByUsername = userService.findByUsername(username);
        var response = UserResponseDTO.fromEntity(userByUsername);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateProfileUser(@Valid @RequestBody UpdateProfileRequestDTO request, @PathVariable UUID id){
        User user =  userService.updateProfileUser(id, request);
        var response = UserResponseDTO.fromEntity(user);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/github")
    public ResponseEntity<UserResponseDTO> linkGithub(@PathVariable UUID id, @RequestBody LinkGithubRequestDTO request){
        User linkGithub = userService.linkGithub(id, request);
        var response = UserResponseDTO.fromEntity(linkGithub);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/github")
    public void unlinkGithub(@PathVariable UUID id){
        userService.unlinkGithub(id);
    }
}
