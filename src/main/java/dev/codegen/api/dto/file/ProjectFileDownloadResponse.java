package dev.codegen.api.dto.file;

public record ProjectFileDownloadResponse(byte[] bytes, String filename, String mimeType) {}
