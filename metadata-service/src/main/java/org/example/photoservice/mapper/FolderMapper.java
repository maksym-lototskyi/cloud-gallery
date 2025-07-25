package org.example.photoservice.mapper;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.response.FolderContentResponseDto;
import org.example.photoservice.dto.response.FolderItemResponseDto;
import org.example.photoservice.dto.response.FolderResponseDto;
import org.example.photoservice.model.Folder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class FolderMapper {

    public static Folder mapToFolder(String name, Folder parent, UUID userId, S3Properties s3Properties) {
        return Folder.builder()
                .objectUUID(UUID.randomUUID())
                .userUUID(userId)
                .name(name)
                .parentFolder(parent)
                .uploadTime(LocalDateTime.now())
                .s3Bucket(s3Properties.getBucketName())
                .build();
    }

    public static FolderResponseDto mapToFolderResponseDto(Folder folder) {
        return FolderResponseDto.builder()
                .name(folder.getName())
                .fileItemId(folder.getObjectUUID())
                .parentFolderId(folder.getParentFolder() != null ? folder.getParentFolder().getObjectUUID() : null)
                .uploadTime(DateTimeFormatter.ofPattern("hh:mm - dd.MM.yyyy").format(folder.getUploadTime()))
                .build();
    }

    public static FolderContentResponseDto mapToFolderContentDto(Folder folder, List<FolderItemResponseDto> folderItems) {
        return FolderContentResponseDto.builder()
                .folderName(folder.getName())
                .folderPath(folder.getFullPath())
                .folderId(folder.getObjectUUID())
                .folderItems(folderItems)
                .parentId(folder.getParentFolder() != null ? folder.getParentFolder().getObjectUUID() : null)
                .build();
    }
}
