package dev.codegen.api.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateMemberRoleRequest(
        @NotBlank @Pattern(regexp = "^(EDITOR|VIEWER)$", message = "Role must be EDITOR or VIEWER")
                String role) {}
