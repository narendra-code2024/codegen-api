package dev.codegen.api.repository;

import dev.codegen.api.entity.Commit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommitRepository extends JpaRepository<Commit, UUID> {
}