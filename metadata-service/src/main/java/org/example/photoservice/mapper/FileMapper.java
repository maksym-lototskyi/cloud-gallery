package org.example.photoservice.mapper;

import org.example.photoservice.dto.FileResponseDto;
import org.example.photoservice.events.S3ObjectUploadEvent;
import org.example.photoservice.events.UploadType;
import org.example.photoservice.model.File;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.UploadStatus;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.format.DateTimeFormatter;

public class FileMapper {
    public static File mapToPhoto(MultipartFile file, Folder folder, String bucketName){
        File photo = new File();
        photo.setUploadStatus(UploadStatus.PENDING);
        photo.setS3Bucket(bucketName);
        photo.setFileType(file.getContentType());
        photo.setName(file.getOriginalFilename());
        photo.setParentFolder(folder);
        return photo;
    }

    public static FileResponseDto mapToPhotoResponse(File file, URL s3Url) {
        return FileResponseDto.builder()
                .path(file.getPathFromRoot())
                .parentFolderId(file.getParentFolder().getFolderUUID())
                .name(file.getName())
                .fileUrl(s3Url.toString())
                .fileType(file.getFileType())
                .uploadTime(DateTimeFormatter.ofPattern("hh:mm - dd.MM.yyyy").format(file.getUploadTime()))
                .build();
    }

    public static S3ObjectUploadEvent mapToEvent(File file, byte[] fileContent) {
        return S3ObjectUploadEvent.builder()
                .objectId(file.getId())
                .s3Key(file.getS3Key())
                .bucketName(file.getS3Bucket())
                .fileType(file.getFileType())
                .fileContent(fileContent)
                .uploadType(UploadType.FILE)
                .build();
    }
}
