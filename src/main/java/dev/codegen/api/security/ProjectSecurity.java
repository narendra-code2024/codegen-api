package dev.codegen.api.security;

import dev.codegen.api.entity.ProjectMember;
import dev.codegen.api.enums.ProjectMemberRole;
import dev.codegen.api.exception.ResourceNotFoundException;
import dev.codegen.api.repository.ProjectMemberRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurity {

    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;

    public boolean isMember(UUID projectId) {
        UUID userId = authUtil.getCurrentUserId();
        if (projectMemberRepository.findByProjectIdAndUserId(projectId, userId).isEmpty()) {
            throw new ResourceNotFoundException("Project not found");
        }
        return true;
    }

    public boolean isOwner(UUID projectId) {
        UUID userId = authUtil.getCurrentUserId();
        ProjectMember member =
                projectMemberRepository
                        .findByProjectIdAndUserId(projectId, userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (member.getRole() != ProjectMemberRole.OWNER) {
            throw new ResourceNotFoundException("Project not found");
        }
        return true;
    }
}
