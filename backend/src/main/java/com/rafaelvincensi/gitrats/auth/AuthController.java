package com.rafaelvincensi.gitrats.auth;

import com.rafaelvincensi.gitrats.auth.dto.LoginRequestDTO;
import com.rafaelvincensi.gitrats.github.GithubService;
import com.rafaelvincensi.gitrats.token.TokenService;
import com.rafaelvincensi.gitrats.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final GithubService githubService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, GithubService githubService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.githubService = githubService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO request){
        var token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var authentication = authenticationManager.authenticate(token);

        User user = (User) authentication.getPrincipal();
        String jwtToken = tokenService.generateToken(user);

        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    @GetMapping("/callback/github")
    public ResponseEntity<?> loginWithGithub(@RequestParam String code){
        User user = githubService.loginWithGithub(code);

        String jwtToken = tokenService.generateToken(user);

        return ResponseEntity.ok(Map.of("token", jwtToken));
    }
}
