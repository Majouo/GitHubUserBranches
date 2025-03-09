package com.github.service;

import com.github.dto.BranchDto;
import com.github.dto.RepositoryDto;
import io.smallrye.mutiny.Uni;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GitHubService {
    private final WebClient webClient;

    public GitHubService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
    }

    public Uni<List<RepositoryDto>> getUserRepositories(String username) {
        // WebClient is used to fetch data about repositories
        Mono<List<RepositoryDto>> mono = webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.error(new UserNotFoundException(username)))
                .bodyToFlux(GitHubRepoResponse.class)
                .filter(repo -> !repo.fork())
                // For every repository function getBranches is called
                .flatMap(repo ->
                        Mono.fromFuture(getBranches(username, repo.name())
                                        .subscribeAsCompletionStage())
                                .map(branches -> new RepositoryDto(repo.name(), repo.owner().login(), branches))
                )
                .collectList();
        return Uni.createFrom().future(mono.toFuture());
    }

    public Uni<List<BranchDto>> getBranches(String username, String repoName) {
        // For a given user and repository list of branches is fetched
        Mono<List<BranchDto>> mono = webClient.get()
                .uri("/repos/{username}/{repo}/branches", username, repoName)
                .retrieve()
                .bodyToFlux(GitHubBranchResponse.class)
                .map(branch -> new BranchDto(branch.name(), branch.commit().sha()))
                .collectList();

        return Uni.createFrom().future(mono.toFuture());
    }

    // Mapping models for GitHub API
    private record GitHubRepoResponse(String name, GitHubOwner owner, boolean fork) {}
    private record GitHubOwner(String login) {}
    private record GitHubBranchResponse(String name, GitHubCommit commit) {}
    private record GitHubCommit(String sha) {}

    // Exception for not found user
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String username) {
            super("User '" + username + "' not found on GitHub");
        }
    }
}