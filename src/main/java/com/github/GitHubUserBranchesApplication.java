package com.github;

import com.github.service.GitHubService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@SpringBootApplication
public class GitHubUserBranchesApplication {
    public static void main(String[] args) {
        SpringApplication.run(GitHubUserBranchesApplication.class, args);
    }
}
