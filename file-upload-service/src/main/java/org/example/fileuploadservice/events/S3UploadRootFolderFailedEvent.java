package org.example.fileuploadservice.events;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class S3UploadRootFolderFailedEvent {
    private UUID userId;
    private Long folderId;
}
