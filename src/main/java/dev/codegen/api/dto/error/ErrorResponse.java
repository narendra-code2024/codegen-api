package dev.codegen.api.dto.error;

import lombok.Builder;
import java.time.Instant;
import java.util.List;

@Builder
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldErrorDto> validationErrors
) {
    @Builder
    public record FieldErrorDto(
        String field,
        String message
    ) {}
}
