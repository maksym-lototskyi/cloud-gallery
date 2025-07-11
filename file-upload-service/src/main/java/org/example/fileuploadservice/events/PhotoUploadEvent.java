package org.example.fileuploadservice.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoUploadEvent {
    private Long photoId;
    private String s3Key;
    private String bucketName;
    private byte[] fileContent;
    private String fileType;
}
