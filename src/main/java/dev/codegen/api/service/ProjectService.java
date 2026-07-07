package dev.codegen.api.service;

import dev.codegen.api.dto.project.CreateProjectRequest;
import dev.codegen.api.dto.project.ProjectResponse;
import dev.codegen.api.dto.project.UpdateProjectRequest;
import dev.codegen.api.entity.ChatSession;
import dev.codegen.api.entity.Project;
import dev.codegen.api.entity.User;
import dev.codegen.api.exception.ResourceNotFoundException;
import dev.codegen.api.mapper.ProjectMapper;
import dev.codegen.api.repository.ChatSessionRepository;
import dev.codegen.api.repository.ProjectRepository;
import dev.codegen.api.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    private final ChatSessionRepository chatSessionRepository;

    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(UUID userId) {
        // return projectRepository.findByOwnerId(userId).stream()
        //         .map(projectMapper::toResponse)
        //         .toList();
        return projectMapper.toResponseList(projectRepository.findByOwnerId(userId));
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID id, UUID userId) {
        Project project = findOwnedProject(id, userId);
        return projectMapper.toResponse(project);
    }

    public ProjectResponse createProject(CreateProjectRequest req, UUID userId) {
        User owner = getUser(userId);

        Project project = new Project();
        project.setOwner(owner);
        project.setName(req.name());
        project = projectRepository.saveAndFlush(project);

        ChatSession session = new ChatSession();
        session.setProject(project);
        session.setTitle(req.name());
        chatSessionRepository.save(session);

        return projectMapper.toResponse(project);
    }

    public ProjectResponse updateProject(UUID id, UpdateProjectRequest req, UUID userId) {
        Project project = findOwnedProject(id, userId);

        if (req.name() != null) {
            project.setName(req.name());
        }
        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    public void deleteProject(UUID id, UUID userId) {
        Project project = findOwnedProject(id, userId);
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    private User getUser(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Project findOwnedProject(UUID id, UUID userId) {
        return projectRepository
                .findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }
}
