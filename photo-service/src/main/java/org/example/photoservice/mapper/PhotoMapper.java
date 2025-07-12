package org.example.photoservice.mapper;

import org.example.photoservice.dto.PhotoResponseDto;
import org.example.photoservice.events.PhotoUploadEvent;
import org.example.photoservice.model.Photo;
import org.example.photoservice.model.PhotoStatus;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PhotoMapper {
    public static Photo mapToPhoto(MultipartFile file, UUID userId, String bucketName){
        Photo photo = new Photo();
        photo.setPhotoStatus(PhotoStatus.PENDING);
        photo.setS3Bucket(bucketName);
        photo.setFileType(file.getContentType());
        photo.setFileName(file.getOriginalFilename());
        photo.setUserId(userId);
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

    public static PhotoUploadEvent mapToEvent(Photo photo, byte[] fileContent) {
        return PhotoUploadEvent.builder()
                .photoId(photo.getId())
                .s3Key(photo.getS3Key())
                .bucketName(photo.getS3Bucket())
                .fileType(photo.getFileType())
                .fileContent(fileContent)
                .build();
    }
}
