package org.example.fileuploadservice.events;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FolderContentDeleteEvent {
    private Long folderId;
    private String bucketName;
    private List<String> s3Keys;
}
