package dev.codegen.api.service;

import dev.codegen.api.entity.Project;
import dev.codegen.api.entity.ProjectMember;
import dev.codegen.api.entity.User;
import dev.codegen.api.enums.ProjectMemberRole;
import dev.codegen.api.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

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
}
