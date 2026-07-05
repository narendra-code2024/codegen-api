package dev.codegen.api.repository;

import dev.codegen.api.entity.Preview;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreviewRepository extends JpaRepository<Preview, UUID> {}
