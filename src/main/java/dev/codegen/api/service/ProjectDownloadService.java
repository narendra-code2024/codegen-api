package dev.codegen.api.service;

import dev.codegen.api.entity.ProjectFile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectDownloadService {

    @PersistenceContext private final EntityManager entityManager;

    public void downloadProjectAsZip(Stream<ProjectFile> fileStream, OutputStream outputStream) {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            fileStream.forEach(
                    file -> {
                        try {
                            String content = file.getContent() != null ? file.getContent() : "";
                            ZipEntry entry = new ZipEntry(file.getFilePath());
                            zos.putNextEntry(entry);
                            zos.write(content.getBytes(StandardCharsets.UTF_8));
                            zos.closeEntry();

                            // Detach from Hibernate context immediately to prevent memory growth
                            entityManager.detach(file);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to write ZIP entry", e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate project ZIP archive", e);
        }
    }
}
