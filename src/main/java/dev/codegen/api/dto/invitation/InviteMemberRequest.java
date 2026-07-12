package dev.codegen.api.dto.invitation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record InviteMemberRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^(EDITOR|VIEWER)$", message = "Role must be EDITOR or VIEWER")
                String role) {}
