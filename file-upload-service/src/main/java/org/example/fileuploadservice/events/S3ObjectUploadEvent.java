package org.example.fileuploadservice.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class S3ObjectUploadEvent {
    private Long objectId;
    private String s3Key;
    private String bucketName;
    private byte[] fileContent;
    private String fileType;
    private UploadType uploadType;
}

