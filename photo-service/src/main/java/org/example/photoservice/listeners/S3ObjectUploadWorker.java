package org.example.photoservice.listeners;

import org.example.photoservice.model.UploadStatus;
import org.example.photoservice.repository.S3ObjectRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class S3ObjectUploadWorker {
    private final S3ObjectRepository s3ObjectRepository;

    public S3ObjectUploadWorker(S3ObjectRepository s3ObjectRepository) {
        this.s3ObjectRepository = s3ObjectRepository;
    }

    @RabbitListener(queues = "s3-uploaded-queue")
    public void handleS3ObjectUploaded(Long objectId) {
        System.out.println("Photo upload completed for ID: " + objectId);
        s3ObjectRepository.findById(objectId).ifPresent(photo -> {
            photo.setUploadStatus(UploadStatus.UPLOADED);
            photo.setUploadTime(LocalDateTime.now());
            s3ObjectRepository.save(photo);
        });
    }

    @RabbitListener(queues = "s3-upload-failed-queue")
    public void handleS3ObjectUploadFailed(Long objectId) {
        System.out.println("Photo upload failed for ID: " + objectId);
        s3ObjectRepository.deleteById(objectId);
    }
}
