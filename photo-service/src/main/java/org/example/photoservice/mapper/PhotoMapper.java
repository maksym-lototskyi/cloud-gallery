package org.example.photoservice.mapper;

import org.example.photoservice.dto.PhotoDto;
import org.example.photoservice.dto.PhotoResponseDto;
import org.example.photoservice.events.PhotoUploadEvent;
import org.example.photoservice.model.Photo;
import org.example.photoservice.model.PhotoStatus;

import java.net.URL;
import java.time.format.DateTimeFormatter;

public class PhotoMapper {
    public static Photo mapToPhoto(PhotoDto photoDto, String clientName){
        String name = photoDto.getPhotoName() == null ?
                photoDto.getFile().getOriginalFilename() : photoDto.getPhotoName();

        String s3Key = clientName + "/" + name;
        Photo photo = new Photo();
        photo.setPhotoStatus(PhotoStatus.PENDING);
        photo.setS3Bucket(photoDto.getBucketName());
        photo.setFileType(photoDto.getFile().getContentType());
        photo.setDescription(photoDto.getDescription());
        photo.setS3Key(s3Key);
        photo.setFileName(name);
        return photo;
    }

    public static PhotoResponseDto mapToPhotoResponse(Photo photo, URL s3Url) {
        return PhotoResponseDto.builder()
                .fileName(photo.getS3Key())
                .fileUrl(s3Url.toString())
                .fileType(photo.getFileType())
                .description(photo.getDescription())
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
