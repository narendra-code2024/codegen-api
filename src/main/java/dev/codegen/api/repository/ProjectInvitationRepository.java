package dev.codegen.api.repository;

import dev.codegen.api.entity.ProjectInvitation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, UUID> {

    Optional<ProjectInvitation> findByProjectIdAndEmail(UUID projectId, String email);
}
