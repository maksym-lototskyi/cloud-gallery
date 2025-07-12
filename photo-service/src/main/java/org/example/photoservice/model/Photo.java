package org.example.photoservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_type", nullable = false)
    private String fileType;
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "s3_bucket", nullable = false)
    private String s3Bucket;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "photo_status", nullable = false)
    private PhotoStatus photoStatus;
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    public Photo() {

    }
    public String getS3Key() {
        return userId + "/" + fileName;
    }
}
