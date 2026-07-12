package dev.codegen.api.service;

import dev.codegen.api.dto.invitation.InviteMemberRequest;
import dev.codegen.api.dto.invitation.ProjectInvitationResponse;
import dev.codegen.api.entity.Project;
import dev.codegen.api.entity.ProjectInvitation;
import dev.codegen.api.entity.User;
import dev.codegen.api.enums.InvitationStatus;
import dev.codegen.api.enums.ProjectMemberRole;
import dev.codegen.api.exception.DuplicateResourceException;
import dev.codegen.api.exception.ResourceNotFoundException;
import dev.codegen.api.mapper.ProjectInvitationMapper;
import dev.codegen.api.repository.ProjectInvitationRepository;
import dev.codegen.api.repository.ProjectMemberRepository;
import dev.codegen.api.repository.ProjectRepository;
import dev.codegen.api.security.AuthUtil;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectInvitationService {

    private final ProjectInvitationRepository projectInvitationRepository;

    private final ProjectRepository projectRepository;

    private final UserService userService;

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMemberService projectMemberService;

    private final ProjectInvitationMapper projectInvitationMapper;

    private final AuthUtil authUtil;

    @PreAuthorize("@projectSecurity.isOwner(#projectId)")
    public ProjectInvitationResponse inviteUser(UUID projectId, InviteMemberRequest req) {
        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Check if the user is already a member
        Optional<User> targetUser = userService.findByEmail(req.email());
        if (targetUser.isPresent()) {
            if (projectMemberRepository
                    .findByProjectIdAndUserId(projectId, targetUser.get().getId())
                    .isPresent()) {
                throw new DuplicateResourceException("User is already a member of this project");
            }
        }

        // If an invitation already exists for this email, delete it to issue a fresh one
        Optional<ProjectInvitation> existingInvite =
                projectInvitationRepository.findByProjectIdAndEmail(projectId, req.email());
        if (existingInvite.isPresent()) {
            projectInvitationRepository.delete(existingInvite.get());
            projectInvitationRepository.flush();
        }

        ProjectInvitation invitation = new ProjectInvitation();
        invitation.setProject(project);
        invitation.setEmail(req.email());
        invitation.setRole(ProjectMemberRole.valueOf(req.role().toUpperCase()));
        invitation.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));

        projectInvitationRepository.save(invitation);
        return projectInvitationMapper.toResponse(invitation);
    }

    @Transactional(readOnly = true)
    public ProjectInvitationResponse getInvitation(UUID invitationId) {
        ProjectInvitation invitation =
                projectInvitationRepository
                        .findById(invitationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        String currentUserEmail = authUtil.getCurrentUserEmail();
        if (!invitation.getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new ResourceNotFoundException("Invitation not found");
        }

        return projectInvitationMapper.toResponse(invitation);
    }

    public void acceptInvitation(UUID invitationId) {
        ProjectInvitation invitation =
                projectInvitationRepository
                        .findById(invitationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation is not pending");
        }

        if (invitation.getExpiresAt().isBefore(Instant.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            projectInvitationRepository.save(invitation);
            throw new IllegalArgumentException("Invitation has expired");
        }

        UUID currentUserId = authUtil.getCurrentUserId();
        String currentUserEmail = authUtil.getCurrentUserEmail();

        if (!invitation.getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new IllegalArgumentException(
                    "This invitation belongs to a different email address");
        }

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(Instant.now());

        User currentUser = userService.getReferenceById(currentUserId);
        projectMemberService.addMember(invitation.getProject(), currentUser, invitation.getRole());

        projectInvitationRepository.save(invitation);
    }
}
