package org.example.photoservice.events;

import java.util.List;

public class FolderContentDeleteEvent {
    private String bucketName;
    private List<String> s3Keys;
}
