package dev.codegen.api.mapper;

import dev.codegen.api.dto.invitation.ProjectInvitationResponse;
import dev.codegen.api.entity.ProjectInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectInvitationMapper {

    @Mapping(target = "projectId", source = "project.id")
    ProjectInvitationResponse toResponse(ProjectInvitation invitation);
}
