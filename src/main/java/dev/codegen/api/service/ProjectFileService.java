package dev.codegen.api.service;

import dev.codegen.api.dto.file.ProjectFileDownloadResponse;
import dev.codegen.api.dto.file.ProjectFileResponse;
import dev.codegen.api.entity.ProjectFile;
import dev.codegen.api.exception.ResourceNotFoundException;
import dev.codegen.api.repository.ProjectFileRepository;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectFileService {

    private final ProjectFileRepository projectFileRepository;
    private final ProjectDownloadService projectDownloadService;

    @PreAuthorize("@projectSecurity.isMember(#projectId)")
    @Transactional(readOnly = true)
    public List<ProjectFileResponse> getFileTree(UUID projectId) {
        return projectFileRepository.findMetadataByProjectId(projectId);
    }

    @PreAuthorize("@projectSecurity.isMember(#projectId)")
    @Transactional(readOnly = true)
    public ProjectFileDownloadResponse getFileDownloadData(UUID projectId, String filePath) {
        ProjectFile file =
                projectFileRepository
                        .findByProjectIdAndFilePath(projectId, filePath)
                        .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        String content = file.getContent() != null ? file.getContent() : "";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        Path fileNamePath = Paths.get(file.getFilePath()).getFileName();
        String filename = fileNamePath != null ? fileNamePath.toString() : "file";

        return new ProjectFileDownloadResponse(bytes, filename, file.getMimeType());
    }

    /**
     * Facade method that queries the database cursor lazily and passes the Stream directly to the
     * ProjectDownloadService for zipping.
     */
    @PreAuthorize("@projectSecurity.isMember(#projectId)")
    @Transactional(readOnly = true)
    public void downloadProjectAsZip(UUID projectId, OutputStream outputStream) {
        try (Stream<ProjectFile> fileStream =
                projectFileRepository.streamAllByProjectId(projectId)) {
            projectDownloadService.downloadProjectAsZip(fileStream, outputStream);
        }
    }
}
