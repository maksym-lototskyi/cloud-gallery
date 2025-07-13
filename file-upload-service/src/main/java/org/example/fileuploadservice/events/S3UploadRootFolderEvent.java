package org.example.fileuploadservice.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class S3UploadRootFolderEvent {
    private UUID userId;
    private Long folderId;
    private String path;
    private String bucketName;
}
