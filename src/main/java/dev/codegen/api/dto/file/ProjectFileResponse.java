package dev.codegen.api.dto.file;

import java.time.Instant;
import java.util.UUID;

public record ProjectFileResponse(
        UUID id, String filePath, String mimeType, int size, Instant updatedAt) {}
