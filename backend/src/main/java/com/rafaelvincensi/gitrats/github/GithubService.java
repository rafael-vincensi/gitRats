package com.rafaelvincensi.gitrats.github;

import com.rafaelvincensi.gitrats.github.dto.GitHubTokenResponseDTO;
import com.rafaelvincensi.gitrats.github.dto.GithubCommitsSearchResponseDTO;
import com.rafaelvincensi.gitrats.github.dto.GithubTokenRequestDTO;
import com.rafaelvincensi.gitrats.github.dto.GithubUserResponseDTO;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDate;

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

        String githubId = githubUser.id();

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

    public Integer getWeeklyCommits(String githubUsername, String accessToken){
        LocalDate today = LocalDate.now();
        LocalDate pastWeek = today.minusDays(7);

        String q = String.format("author:%s committer-date:%s..%s", githubUsername, pastWeek, today);

        try {
            GithubCommitsSearchResponseDTO response = restClient.get()
                    .uri("https://api.github.com/search/commits?q={q}", q)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github+json")
                    .retrieve()
                    .body(GithubCommitsSearchResponseDTO.class);

            Integer total = 0;

            if (response != null && response.total_count() != null) {
                total = response.total_count();
            }

            return total;
        }catch (Exception e){
            System.err.printf("Github error: " + e.getMessage());
            return 0;
        }
    }
}
