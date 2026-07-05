package dev.codegen.api.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "project_files")
@Getter
@Setter
@NoArgsConstructor
public class ProjectFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String storageKey;

    private String mimeType;

    @UpdateTimestamp private Instant updatedAt;

    private Instant deletedAt;
}
