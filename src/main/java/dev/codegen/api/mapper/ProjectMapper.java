package dev.codegen.api.mapper;

import dev.codegen.api.dto.project.CreateProjectRequest;
import dev.codegen.api.dto.project.ProjectResponse;
import dev.codegen.api.entity.Project;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(CreateProjectRequest dto);

    ProjectResponse toResponse(Project project);

    List<ProjectResponse> toResponseList(List<Project> projects);
}
