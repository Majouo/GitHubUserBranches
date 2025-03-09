package com.github.dto;

import java.util.List;

public record RepositoryDto(String repositoryName, String ownerLogin, List<BranchDto> branches) {}