package dev.codegen.api.repository;

import dev.codegen.api.dto.file.ProjectFileResponse;
import dev.codegen.api.entity.ProjectFile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, UUID> {

    // Constructor projection to fetch file tree metadata only, avoiding loading content into memory
    @Query(
            "SELECT new dev.codegen.api.dto.file.ProjectFileResponse(pf.id, pf.filePath, pf.mimeType, pf.size, pf.updatedAt) "
                    + "FROM ProjectFile pf WHERE pf.project.id = :projectId")
    List<ProjectFileResponse> findMetadataByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT pf FROM ProjectFile pf WHERE pf.project.id = :projectId")
    Stream<ProjectFile> streamAllByProjectId(@Param("projectId") UUID projectId);

    Optional<ProjectFile> findByProjectIdAndFilePath(UUID projectId, String filePath);
}
