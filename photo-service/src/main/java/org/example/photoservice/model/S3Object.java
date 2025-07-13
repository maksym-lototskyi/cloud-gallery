package org.example.photoservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "object_type")
public abstract class S3Object {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String name;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "photo_status", nullable = false)
    private UploadStatus uploadStatus;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder parentFolder;
    @Column(name = "s3_bucket", nullable = false)
    private String s3Bucket;
}
