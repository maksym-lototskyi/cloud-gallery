package org.example.fileuploadservice.events;

public class S3ObjectUploadResultEvent {
    private final Long objectId;

    public S3ObjectUploadResultEvent(Long objectId) {
        this.objectId = objectId;
    }
}
