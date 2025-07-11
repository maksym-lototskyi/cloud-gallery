package org.example.photoservice.service;

import org.example.photoservice.dto.PhotoDto;
import org.example.photoservice.dto.PhotoResponseDto;
import org.example.photoservice.exception.PhotoUploadException;
import org.example.photoservice.mapper.PhotoMapper;
import org.example.photoservice.model.Photo;
import org.example.photoservice.repository.PhotoRepository;
import org.example.photoservice.model.PhotoStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final RabbitTemplate rabbitTemplate;

    public PhotoService(PhotoRepository photoRepository, S3Client s3Client, S3Presigner s3Presigner, RabbitTemplate rabbitTemplate) {
        this.photoRepository = photoRepository;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void uploadPhoto(PhotoDto photoDto, String userId) {
        Photo photo = PhotoMapper.mapToPhoto(photoDto, userId);
        photoRepository.save(photo);

        try {
            rabbitTemplate.convertAndSend("photo-exchange", "photo-upload-key", PhotoMapper.mapToEvent(photo, photoDto.getFile().getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PhotoResponseDto getPhoto(String s3Key){
        Photo photo = photoRepository.findByS3Key(s3Key)
                .orElseThrow(() -> new PhotoUploadException("Photo not found with key: " + s3Key));

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(photo.getS3Bucket())
                .key(photo.getS3Key())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(10))
                .build();
        URL s3Url = s3Presigner.presignGetObject(presignRequest)
                .url();

        return PhotoMapper.mapToPhotoResponse(photo, s3Url);
    }

    public List<PhotoResponseDto> getPhotoPage(int page, int pageSize){

        return photoRepository.findAll(Pageable.ofSize(pageSize).withPage(page))
                .getContent()
                .stream()
                .filter(photo -> photo.getPhotoStatus() == PhotoStatus.UPLOADED)
                .map(photo -> getPhoto(photo.getS3Key()))
                .toList();
    }

    public void deletePhotoById(Long id){
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new PhotoUploadException("Photo not found with id: " + id));

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(photo.getS3Bucket())
                .key(photo.getS3Key())
                .build());

        photoRepository.delete(photo);
    }


}
