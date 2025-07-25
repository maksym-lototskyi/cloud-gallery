package org.example.photoservice.mapper;

import org.example.photoservice.dto.response.FilePreviewResponseDto;
import org.example.photoservice.dto.response.FileResponseDto;
import org.example.photoservice.events.S3ObjectUploadEvent;
import org.example.photoservice.model.File;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.UploadStatus;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileMapper {
    public static File mapToFile(MultipartFile file, Folder folder, String bucketName){
        File fileEntity = new File();
        fileEntity.setUploadStatus(UploadStatus.PENDING);
        fileEntity.setS3Bucket(bucketName);
        fileEntity.setFileType(file.getContentType());
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setParentFolder(folder);
        fileEntity.setObjectUUID(UUID.randomUUID());
        fileEntity.setUserUUID(folder.getUserUUID());
        return fileEntity;
    }

    public static FilePreviewResponseDto mapToFilePreview(File file) {
        return FilePreviewResponseDto.builder()
                .parentFolderId(file.getParentFolder().getObjectUUID())
                .name(file.getName())
                .fileType(file.getFileType())
                .fileItemId(file.getObjectUUID())
                .uploadTime(DateTimeFormatter.ofPattern("hh:mm - dd.MM.yyyy").format(file.getUploadTime()))
                .build();
    }

    public static FileResponseDto mapToDetails(File file, URL url){
        return FileResponseDto.builder()
                .parentFolderId(file.getParentFolder().getObjectUUID())
                .name(file.getName())
                .fileType(file.getFileType())
                .fileItemId(file.getObjectUUID())
                .uploadTime(DateTimeFormatter.ofPattern("hh:mm - dd.MM.yyyy").format(file.getUploadTime()))
                .url(url.toString())
                .build();
    }

    public static S3ObjectUploadEvent mapToEvent(File file, byte[] fileContent) {
        return S3ObjectUploadEvent.builder()
                .objectId(file.getId())
                .s3Key(file.getObjectUUID().toString())
                .bucketName(file.getS3Bucket())
                .fileType(file.getFileType())
                .fileContent(fileContent)
                .build();
    }
}
