package dev.codegen.api.repository;

import dev.codegen.api.entity.Preview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PreviewRepository extends JpaRepository<Preview, UUID> {
}