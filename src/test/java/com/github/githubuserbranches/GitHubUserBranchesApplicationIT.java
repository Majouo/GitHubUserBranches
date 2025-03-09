package com.github.githubuserbranches;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class GitHubUserBranchesApplicationIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void happyPathIntegrationTest() {
        // GIVEN: Existing GitHub user
        String username = "Majouo";

        // WHEN: Sending request to API
        List<Map> responseBody = webTestClient.get()
                .uri("/github/repos/" + username)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Map.class)
                .returnResult()
                .getResponseBody();

        // THEN: Response body cannot be empty
        assertThat(responseBody).isNotNull().isNotEmpty();

        for (Map<String, Object> repo : responseBody) {
            // THEN: Every repository must contain keys "repositoryName", "ownerLogin" which corresponding values are Strings and aren't empty
            assertThat(repo).containsOnlyKeys("repositoryName", "ownerLogin", "branches");
            assertThat(repo.get("repositoryName")).isNotNull().isInstanceOf(String.class);
            assertThat((String)repo.get("repositoryName")).isNotEmpty();
            assertThat(repo.get("ownerLogin")).isNotNull().isInstanceOf(String.class);
            assertThat((String)repo.get("ownerLogin")).isNotEmpty();
            // THEN: Every repository must contain key "branches" which is a non-empty List of branches
            assertThat(repo.get("branches")).isNotNull().isInstanceOf(List.class);
            List<Map<String, Object>> branches = (List<Map<String, Object>>) repo.get("branches");
            assertThat(branches).isNotEmpty();
            for (Map<String, Object> branch : branches) {
                // THEN: Every branch contains keys "branchName" i "lastCommitSha" which corresponding values are Strings and aren't empty
                assertThat(branch).isNotNull().isNotEmpty().isInstanceOf(Map.class);
                assertThat(branch).containsOnlyKeys("branchName", "lastCommitSha");
                assertThat(branch.get("branchName")).isNotNull().isInstanceOf(String.class);
                assertThat((String)branch.get("branchName")).isNotEmpty();
                assertThat(branch.get("lastCommitSha")).isNotNull().isInstanceOf(String.class);
                // THEN: Every branch contains key "lastCommitSha" which value must be a proper SHA-1 hash that size has to be equal 20 bytes
                byte[] sha1Bytes = HexFormat.of().parseHex((String)branch.get("lastCommitSha"));
                assertThat(sha1Bytes.length).isEqualTo(20);
            }
        }
    }
}