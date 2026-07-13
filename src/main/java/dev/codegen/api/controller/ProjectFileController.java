package dev.codegen.api.controller;

import dev.codegen.api.dto.file.ProjectFileDownloadResponse;
import dev.codegen.api.dto.file.ProjectFileResponse;
import dev.codegen.api.service.ProjectFileService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/files")
@RequiredArgsConstructor
public class ProjectFileController {

    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<List<ProjectFileResponse>> getFileTree(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectFileService.getFileTree(projectId));
    }

    @GetMapping("/content")
    public ResponseEntity<byte[]> getFileContent(
            @PathVariable UUID projectId, @RequestParam("path") String path) {

        ProjectFileDownloadResponse downloadData =
                projectFileService.getFileDownloadData(projectId, path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadData.mimeType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadData.filename() + "\"")
                .contentLength(downloadData.bytes().length)
                .body(downloadData.bytes());
    }

    @GetMapping("/download")
    public void downloadProject(@PathVariable UUID projectId, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/zip");
        response.setHeader(
                "Content-Disposition", "attachment; filename=\"project-" + projectId + ".zip\"");
        projectFileService.downloadProjectAsZip(projectId, response.getOutputStream());
    }
}
