package org.example.photoservice.events;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3UploadRootFolderEvent {
    private UUID userId;
    private Long folderId;
    private String s3Key;
    private String bucketName;
}
