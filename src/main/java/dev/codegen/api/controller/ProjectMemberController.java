package dev.codegen.api.controller;

import dev.codegen.api.dto.member.ProjectMemberResponse;
import dev.codegen.api.dto.member.UpdateMemberRoleRequest;
import dev.codegen.api.service.ProjectMemberService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectMemberService.getMembers(projectId));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ProjectMemberResponse> updateMemberRole(
            @PathVariable UUID projectId,
            @PathVariable UUID memberId,
            @Valid @RequestBody UpdateMemberRoleRequest req) {
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId, req));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID projectId, @PathVariable UUID memberId) {
        projectMemberService.removeMember(projectId, memberId);
        return ResponseEntity.noContent().build();
    }
}
