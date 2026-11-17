package com.rafaelvincensi.gitrats.github;

import org.springframework.http.MediaType;
import com.rafaelvincensi.gitrats.github.dto.GithubCommitsSearchResponseDTO;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

    @Mock
    private UserRepository userRepository;

    private MockRestServiceServer mockServer;
    private GithubService githubService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        githubService = new GithubService(userRepository);

        ReflectionTestUtils.setField(githubService, "restClient", builder.build());
        ReflectionTestUtils.setField(githubService, "clientId", "frank-ocean-id");
        ReflectionTestUtils.setField(githubService, "clientSecret", "frank-ocean-id");
        ReflectionTestUtils.setField(githubService, "redirectUrl", "http://localhost:8080/callback");
    }

    @Test
    @DisplayName("Should get weekly commits successfully")
    void ShouldGetWeeklyCommitsSuccessfully(){
        User user = new User();
        user.setGithubUsername("rafael-vincensi");
        String accessToken = "token-de-acesso";

        GithubCommitsSearchResponseDTO mockResponse = new GithubCommitsSearchResponseDTO(54);

        mockServer.expect(requestTo(startsWith("https://api.github.com/search/commits")))
                .andExpect(header("Authorization", "Bearer " + accessToken))
                .andRespond(withSuccess("{\"total_count\": 54}", MediaType.APPLICATION_JSON));

        Integer result = githubService.getWeeklyCommits(user.getGithubUsername(), accessToken);

        assertNotNull(result);
        assertEquals(54, result);
    }
}
