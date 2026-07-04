package dev.codegen.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    T data,
    ApiError error,
    Instant timestamp
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ApiError(
        String message,
        List<FieldErrorDto> fieldErrors
    ) {}

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, new ApiError(message, null), Instant.now());
    }

    public static <T> ApiResponse<T> error(String message, List<FieldErrorDto> fieldErrors) {
        return new ApiResponse<>(null, new ApiError(message, fieldErrors), Instant.now());
    }
}
