package com.github.controller;

import com.github.dto.RepositoryDto;
import com.github.service.GitHubService;
import io.smallrye.mutiny.Uni;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/github/repos")
public class GitHubController {
    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }
    @GetMapping("/{username}")
    public Uni<List<RepositoryDto>> getRepositories(@PathVariable String username) {
        return gitHubService.getUserRepositories(username);
    }

    // Exception Handler for not found user
    @ExceptionHandler(GitHubService.UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Uni<Map<String, String>> handleUserNotFound(GitHubService.UserNotFoundException ex) {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        response.put("message", ex.getMessage());
        return Uni.createFrom().item(response);
    }
}
