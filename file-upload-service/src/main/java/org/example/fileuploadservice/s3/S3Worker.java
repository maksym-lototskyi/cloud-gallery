package org.example.fileuploadservice.s3;

import org.example.fileuploadservice.events.PhotoUploadEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;

@Service
public class S3Worker {
    private final S3Client s3Client;
    private final RabbitTemplate rabbitTemplate;

    public S3Worker(S3Client s3Client, RabbitTemplate rabbitTemplate) {
        this.s3Client = s3Client;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "photo-queue")
    public void processPhotoUpload(PhotoUploadEvent photoEvent) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(photoEvent.getBucketName())
                .key(photoEvent.getS3Key())
                .contentType(photoEvent.getFileType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(photoEvent.getFileContent()));
        } catch (Exception e) {
            System.out.println("Upload failed");
            rabbitTemplate.convertAndSend("photo-exchange", "photo-upload-failed-key", photoEvent.getPhotoId());
            /*photoRepository.deleteById(photoEvent.getPhotoId());
            throw new PhotoUploadException(e.getMessage());*/
            return;
        }
       /* Photo photo = photoRepository.findById(photoEvent.getPhotoId())
                .orElseThrow(() -> new PhotoUploadException("Photo not found with ID: " + photoEvent.getPhotoId()));

        photo.setPhotoStatus(PhotoStatus.UPLOADED);
        photo.setUploadTime(LocalDateTime.now());*/
        System.out.println("Upload successful");
        rabbitTemplate.convertAndSend("photo-exchange", "photo-uploaded-key", photoEvent.getPhotoId());
    }
}
