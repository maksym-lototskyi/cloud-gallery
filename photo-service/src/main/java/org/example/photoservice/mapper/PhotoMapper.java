package org.example.photoservice.mapper;

import org.example.photoservice.dto.PhotoResponseDto;
import org.example.photoservice.events.S3ObjectUploadEvent;
import org.example.photoservice.events.UploadType;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.Photo;
import org.example.photoservice.model.UploadStatus;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.format.DateTimeFormatter;

public class PhotoMapper {
    public static Photo mapToPhoto(MultipartFile file, Folder folder, String bucketName){
        Photo photo = new Photo();
        photo.setUploadStatus(UploadStatus.PENDING);
        photo.setS3Bucket(bucketName);
        photo.setFileType(file.getContentType());
        photo.setName(file.getOriginalFilename());
        photo.setParentFolder(folder);
        return photo;
    }

    public static PhotoResponseDto mapToPhotoResponse(Photo photo, URL s3Url) {
        return PhotoResponseDto.builder()
                .fileName(photo.getS3Key())
                .fileUrl(s3Url.toString())
                .fileType(photo.getFileType())
                .uploadTime(DateTimeFormatter.ofPattern("hh:mm - dd.MM.yyyy").format(photo.getUploadTime()))
                .build();
    }

    public static S3ObjectUploadEvent mapToEvent(Photo photo, byte[] fileContent) {
        return S3ObjectUploadEvent.builder()
                .objectId(photo.getId())
                .s3Key(photo.getS3Key())
                .bucketName(photo.getS3Bucket())
                .fileType(photo.getFileType())
                .fileContent(fileContent)
                .uploadType(UploadType.FILE)
                .build();
    }
}
