package dev.codegen.api.service;

import dev.codegen.api.dto.member.ProjectMemberResponse;
import dev.codegen.api.dto.member.UpdateMemberRoleRequest;
import dev.codegen.api.entity.Project;
import dev.codegen.api.entity.ProjectMember;
import dev.codegen.api.entity.User;
import dev.codegen.api.enums.ProjectMemberRole;
import dev.codegen.api.exception.ResourceNotFoundException;
import dev.codegen.api.mapper.ProjectMemberMapper;
import dev.codegen.api.repository.ProjectMemberRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberMapper projectMemberMapper;

    public void addOwner(Project project) {
        if (projectMemberRepository.existsOwnerByProjectId(project.getId())) {
            throw new IllegalArgumentException("Project already has an owner");
        }
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(project.getCreatedBy());
        member.setRole(ProjectMemberRole.OWNER);
        projectMemberRepository.save(member);
    }

    public void addMember(Project project, User user, ProjectMemberRole role) {
        if (projectMemberRepository
                .findByProjectIdAndUserId(project.getId(), user.getId())
                .isPresent()) {
            throw new IllegalArgumentException("User is already a member of this project");
        }
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(role);
        projectMemberRepository.save(member);
    }

    @PreAuthorize("@projectSecurity.isMember(#projectId)")
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMembers(UUID projectId) {
        return projectMemberMapper.toResponseList(
                projectMemberRepository.findByProjectId(projectId));
    }

    @PreAuthorize("@projectSecurity.isOwner(#projectId)")
    public ProjectMemberResponse updateMemberRole(
            UUID projectId, UUID memberId, UpdateMemberRoleRequest req) {
        ProjectMember member =
                projectMemberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!member.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Member does not belong to this project");
        }

        if (member.getRole() == ProjectMemberRole.OWNER) {
            throw new IllegalArgumentException("Cannot change the role of the project owner");
        }

        member.setRole(ProjectMemberRole.valueOf(req.role().toUpperCase()));
        member = projectMemberRepository.save(member);
        return projectMemberMapper.toResponse(member);
    }

    @PreAuthorize("@projectSecurity.isOwner(#projectId)")
    public void removeMember(UUID projectId, UUID memberId) {
        ProjectMember member =
                projectMemberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!member.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Member does not belong to this project");
        }

        if (member.getRole() == ProjectMemberRole.OWNER) {
            throw new IllegalArgumentException("Cannot remove the project owner");
        }

        member.setDeletedAt(Instant.now());
        projectMemberRepository.save(member);
    }
}
