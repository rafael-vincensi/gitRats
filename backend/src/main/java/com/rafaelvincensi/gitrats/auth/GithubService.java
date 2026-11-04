package com.rafaelvincensi.gitrats.auth;

import com.rafaelvincensi.gitrats.auth.dto.GitHubTokenResponseDTO;
import com.rafaelvincensi.gitrats.auth.dto.GithubTokenRequestDTO;
import com.rafaelvincensi.gitrats.auth.dto.GithubUserResponseDTO;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GithubService {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.url}")
    private String redirectUrl;

    private final RestClient restClient = RestClient.create();

    private final UserRepository userRepository;

    public GithubService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loginWithGithub(String code){
    var requestBody = new GithubTokenRequestDTO(clientId, clientSecret, code, redirectUrl);

        GitHubTokenResponseDTO tokenResponse = restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .header("Accept", "application/json")
                .body(requestBody)
                .retrieve()
                .body(GitHubTokenResponseDTO.class);

        String accessToken = tokenResponse.access_token();

        GithubUserResponseDTO githubUser = restClient.get()
                .uri("https://api.github.com/user")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GithubUserResponseDTO.class);

        String githubId = githubUser.id().toString();

        User user = userRepository.findByGithubId(githubId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setGithubId(githubId);
                    newUser.setGithubUsername(githubUser.login());
                    newUser.setUsername(githubUser.login());
                    newUser.setEmail(githubUser.login() + "@github.com");

                    return userRepository.save(newUser);
        });
        return user;
    }

}
