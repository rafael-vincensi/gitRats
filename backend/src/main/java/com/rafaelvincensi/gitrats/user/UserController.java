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
    public User createUser(@RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable UUID id){
        return userService.findById(id);
    }

    @GetMapping("/username/{username}")
    public User findByUsername(@PathVariable String username){
        return userService.findByUsername(username);
    }

    @PatchMapping("/{id}")
    public User updateProfileUser(@PathVariable UUID id, @RequestBody UpdateProfileRequest request){
        return userService.updateProfileUser(id, request);
    }

    @PostMapping("/{id}/github")
    public User linkGithub(@PathVariable UUID id, @RequestBody LinkGithubRequest request){
        return userService.linkGithub(id, request);
    }
}
