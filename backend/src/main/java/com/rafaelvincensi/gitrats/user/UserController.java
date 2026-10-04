package com.rafaelvincensi.gitrats.user;

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
    public UserResponse createUser(@RequestBody CreateUserRequest request){
        User user = userService.createUser(request);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getGithubUsername()
        );
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id){
        User user = userService.findById(id);

        return new UserResponse(
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
    public UserResponse updateProfileUser(@PathVariable UUID id, @RequestBody UpdateProfileRequest request){
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
    public UserResponse linkGithub(@PathVariable UUID id, @RequestBody LinkGithubRequest request){
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
