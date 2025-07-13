package org.example.photoservice.mapper;

import org.example.photoservice.events.S3ObjectUploadEvent;
import org.example.photoservice.events.UploadType;
import org.example.photoservice.model.Folder;

public class FolderMapper {
    public static S3ObjectUploadEvent mapToEvent(Folder folder){
        return S3ObjectUploadEvent.builder()
                .bucketName(folder.getS3Bucket())
                .objectId(folder.getId())
                .s3Key(folder.getFullPath())
                .fileContent(null)
                .fileType(null)
                .uploadType(UploadType.FOLDER)
                .build();
    }

}
