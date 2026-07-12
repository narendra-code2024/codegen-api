package dev.codegen.api.repository;

import dev.codegen.api.entity.ProjectMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {

    Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);

    List<ProjectMember> findByProjectId(UUID projectId);

    @Query(
            "SELECT (COUNT(pm) > 0) FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role = dev.codegen.api.enums.ProjectMemberRole.OWNER")
    boolean existsOwnerByProjectId(@Param("projectId") UUID projectId);
}
