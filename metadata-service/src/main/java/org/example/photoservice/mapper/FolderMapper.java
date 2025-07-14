package org.example.photoservice.mapper;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.FolderRequestDto;
import org.example.photoservice.dto.FolderResponseDto;
import org.example.photoservice.events.S3ObjectUploadEvent;
import org.example.photoservice.events.UploadType;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.UploadStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public static Folder mapToFolder(String name, Folder parent, UUID userId, S3Properties s3Properties) {
        return Folder.builder()
                .folderUUID(UUID.randomUUID())
                .userUUID(userId)
                .name(name)
                .parentFolder(parent)
                .uploadStatus(UploadStatus.PENDING)
                .s3Bucket(s3Properties.getBucketName())
                .build();
    }

    public static FolderResponseDto mapToFolderResponseDto(Folder folder) {
        return FolderResponseDto.builder()
                .name(folder.getName())
                .path(folder.getFullPath())
                .parentFolderId(folder.getParentFolder() != null ? folder.getParentFolder().getId() : null)
                .build();
    }
}
