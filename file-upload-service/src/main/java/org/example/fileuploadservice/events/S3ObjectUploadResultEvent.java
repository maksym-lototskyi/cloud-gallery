package org.example.fileuploadservice.events;

public class S3ObjectUploadResultEvent {
    private final Long objectId;
    private final UploadType uploadType;

    public S3ObjectUploadResultEvent(Long objectId, UploadType uploadType) {
        this.objectId = objectId;
        this.uploadType = uploadType;
    }
}
