package dev.codegen.api.dto.project;

import jakarta.validation.constraints.NotBlank;

public record UpdateProjectRequest(@NotBlank String name) {}
