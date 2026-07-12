package dev.codegen.api.dto.member;

import dev.codegen.api.enums.ProjectMemberRole;
import java.time.Instant;
import java.util.UUID;

public record ProjectMemberResponse(
        UUID id,
        UUID userId,
        String email,
        String username,
        ProjectMemberRole role,
        Instant joinedAt) {}
