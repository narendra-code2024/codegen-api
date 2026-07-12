package dev.codegen.api.dto.invitation;

import dev.codegen.api.enums.InvitationStatus;
import dev.codegen.api.enums.ProjectMemberRole;
import java.time.Instant;
import java.util.UUID;

public record ProjectInvitationResponse(
        UUID id,
        UUID projectId,
        String email,
        ProjectMemberRole role,
        InvitationStatus status,
        Instant invitedAt,
        Instant expiresAt,
        Instant acceptedAt) {}
