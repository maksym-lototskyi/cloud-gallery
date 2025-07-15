package org.example.photoservice.mapper;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.FolderContentDto;
import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.dto.FolderRequestDto;
import org.example.photoservice.dto.FolderResponseDto;
import org.example.photoservice.events.S3ObjectUploadEvent;
import org.example.photoservice.events.UploadType;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.UploadStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class FolderMapper {

    public static S3ObjectUploadEvent mapToEvent(Folder folder){
        return S3ObjectUploadEvent.builder()
                .bucketName(folder.getS3Bucket())
                .objectId(folder.getId())
                .s3Key(folder.getS3Key())
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
                .folderId(folder.getFolderUUID())
                .parentFolderId(folder.getParentFolder() != null ? folder.getParentFolder().getFolderUUID() : null)
                .uploadTime(DateTimeFormatter.ofPattern("hh:mm - dd.MM.yyyy").format(folder.getUploadTime()))
                .build();
    }

    public static FolderContentDto mapToFolderContentDto(Folder folder, List<FolderItemResponseDto> folderItems) {
        return FolderContentDto.builder()
                .folderName(folder.getName())
                .folderPath(folder.getFullPath())
                .folderId(folder.getFolderUUID())
                .folderItems(folderItems)
                .parentId(folder.getParentFolder() != null ? folder.getParentFolder().getFolderUUID() : null)
                .build();
    }
}
