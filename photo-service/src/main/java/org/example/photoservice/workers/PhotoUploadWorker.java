package org.example.photoservice.workers;

import org.example.photoservice.repository.PhotoRepository;
import org.example.photoservice.model.PhotoStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PhotoUploadWorker {
    private final PhotoRepository photoRepository;

    public PhotoUploadWorker(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @RabbitListener(queues = "photo-uploaded-queue")
    public void handlePhotoUploaded(Long photoId) {
        System.out.println("Photo upload completed for ID: " + photoId);
        photoRepository.findById(photoId).ifPresent(photo -> {
            photo.setPhotoStatus(PhotoStatus.UPLOADED);
            photo.setUploadTime(LocalDateTime.now());
            photoRepository.save(photo);
        });
    }

    @RabbitListener(queues = "photo-upload-failed-queue")
    public void handlePhotoUploadFailed(Long photoId) {
        System.out.println("Photo upload failed for ID: " + photoId);
        photoRepository.deleteById(photoId);
    }
}
