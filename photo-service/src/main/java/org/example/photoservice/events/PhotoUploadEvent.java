package org.example.photoservice.events;


import lombok.*;

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
