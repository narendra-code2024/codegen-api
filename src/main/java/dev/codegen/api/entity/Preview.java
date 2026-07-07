package dev.codegen.api.entity;

import dev.codegen.api.enums.PreviewStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "previews")
@Getter
@Setter
@NoArgsConstructor
public class Preview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String previewUrl;

    @Enumerated(EnumType.STRING)
    private PreviewStatus status;

    @CreationTimestamp private Instant startedAt;

    private Instant expiresAt;
}
