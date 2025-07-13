package org.example.photoservice.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class S3UploadRootFolderFailedEvent {
    private UUID userId;
    private Long folderId;
}
