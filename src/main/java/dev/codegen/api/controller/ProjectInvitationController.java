package dev.codegen.api.controller;

import dev.codegen.api.dto.invitation.InviteMemberRequest;
import dev.codegen.api.dto.invitation.ProjectInvitationResponse;
import dev.codegen.api.service.ProjectInvitationService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectInvitationController {

    private final ProjectInvitationService projectInvitationService;

    @PostMapping("/projects/{projectId}/invitations")
    public ResponseEntity<ProjectInvitationResponse> inviteUser(
            @PathVariable UUID projectId, @Valid @RequestBody InviteMemberRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectInvitationService.inviteUser(projectId, req));
    }

    @GetMapping("/invitations/{id}")
    public ResponseEntity<ProjectInvitationResponse> getInvitation(@PathVariable UUID id) {
        return ResponseEntity.ok(projectInvitationService.getInvitation(id));
    }

    @PostMapping("/invitations/{id}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable UUID id) {
        projectInvitationService.acceptInvitation(id);
        return ResponseEntity.noContent().build();
    }
}
