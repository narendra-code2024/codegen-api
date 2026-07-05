package dev.codegen.api.repository;

import dev.codegen.api.entity.ChatSession;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {}
