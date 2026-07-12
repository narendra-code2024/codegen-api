package dev.codegen.api.service;

import dev.codegen.api.dto.project.CreateProjectRequest;
import dev.codegen.api.dto.project.ProjectResponse;
import dev.codegen.api.dto.project.UpdateProjectRequest;
import dev.codegen.api.entity.Project;
import dev.codegen.api.exception.ResourceNotFoundException;
import dev.codegen.api.mapper.ProjectMapper;
import dev.codegen.api.repository.ProjectRepository;
import dev.codegen.api.security.AuthUtil;
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
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UserService userService;

    private final ProjectMemberService projectMemberService;

    private final ProjectMapper projectMapper;

    private final AuthUtil authUtil;

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects() {
        UUID userId = authUtil.getCurrentUserId();
        return projectMapper.toResponseList(projectRepository.getProjectsAccessibleByUser(userId));
    }

    public ProjectResponse createProject(CreateProjectRequest req) {
        UUID userId = authUtil.getCurrentUserId();

        Project project = projectMapper.toEntity(req);
        project.setCreatedBy(userService.getReferenceById(userId));
        project = projectRepository.saveAndFlush(project);

        projectMemberService.addOwner(project);

        return projectMapper.toResponse(project);
    }

    @PreAuthorize("@projectSecurity.isMember(#id)")
    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID id) {
        Project project =
                projectRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toResponse(project);
    }

    @PreAuthorize("@projectSecurity.isOwner(#id)")
    public ProjectResponse updateProject(UUID id, UpdateProjectRequest req) {
        Project project =
                projectRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        project.setName(req.name());
        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @PreAuthorize("@projectSecurity.isOwner(#id)")
    public void deleteProject(UUID id) {
        Project project =
                projectRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }
}
