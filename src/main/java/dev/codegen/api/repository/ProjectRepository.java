package dev.codegen.api.repository;

import dev.codegen.api.entity.Project;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {}
