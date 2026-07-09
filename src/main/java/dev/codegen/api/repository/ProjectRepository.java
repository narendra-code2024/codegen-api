package dev.codegen.api.repository;

import dev.codegen.api.entity.Project;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query(
            """
            SELECT p FROM Project p
            JOIN ProjectMember pm ON pm.project = p
            WHERE pm.user.id = :userId
            """)
    List<Project> getProjectsAccessibleByUser(@Param("userId") UUID userId);
}
