package dev.codegen.api.mapper;

import dev.codegen.api.dto.member.ProjectMemberResponse;
import dev.codegen.api.entity.ProjectMember;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "joinedAt", source = "createdAt")
    ProjectMemberResponse toResponse(ProjectMember member);

    List<ProjectMemberResponse> toResponseList(List<ProjectMember> members);
}
