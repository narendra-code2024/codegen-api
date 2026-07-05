package dev.codegen.api.repository;

import dev.codegen.api.entity.ChatMessage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {}
