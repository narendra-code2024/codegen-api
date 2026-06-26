package dev.codegen.api.mapper;

import dev.codegen.api.dto.auth.SignupRequest;
import dev.codegen.api.dto.auth.UserResponse;
import dev.codegen.api.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest dto);

    UserResponse toResponse(User user);
}
