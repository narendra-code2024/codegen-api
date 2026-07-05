package dev.codegen.api.repository;

import dev.codegen.api.entity.Commit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitRepository extends JpaRepository<Commit, UUID> {}
