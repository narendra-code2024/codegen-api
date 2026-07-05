package dev.codegen.api.repository;

import dev.codegen.api.entity.ProjectFile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, UUID> {}
