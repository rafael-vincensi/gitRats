package com.rafaelvincensi.gitrats.auth;

import com.rafaelvincensi.gitrats.auth.dto.LoginRequestDTO;
import com.rafaelvincensi.gitrats.github.GithubService;
import com.rafaelvincensi.gitrats.token.Token;
import com.rafaelvincensi.gitrats.token.TokenService;
import com.rafaelvincensi.gitrats.user.User;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        String accessToken = tokenService.generateToken(user);
        Token refreshToken = tokenService.createRefreshToken(user);

        return ResponseEntity.ok(Map.of("accessToken", accessToken, "refreshToken", refreshToken.getToken()));
    }

    @GetMapping("/callback/github")
    public ResponseEntity<?> loginWithGithub(@RequestParam String code){
        User user = githubService.loginWithGithub(code);

        String accessToken = tokenService.generateToken(user);
        Token refreshToken = tokenService.createRefreshToken(user);

        return ResponseEntity.ok(Map.of("accessToken", accessToken, "refreshToken", refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        if (requestRefreshToken == null || requestRefreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required!"));
        }
            Token refreshToken = tokenService.verifyRefreshToken(requestRefreshToken);
            String newAccessToken = tokenService.generateToken(refreshToken.getUser());

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", refreshToken.getToken()));
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(@AuthenticationPrincipal User user){
            if (user != null){
                tokenService.deleteByUser(user);
            }

            return ResponseEntity.noContent().build();
        }
    }

