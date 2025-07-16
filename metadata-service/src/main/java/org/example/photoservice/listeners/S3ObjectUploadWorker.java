package org.example.photoservice.listeners;

import org.example.photoservice.model.UploadStatus;
import org.example.photoservice.repository.FolderItemRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class S3ObjectUploadWorker {
    private final FolderItemRepository folderItemRepository;

    public S3ObjectUploadWorker(FolderItemRepository folderItemRepository) {
        this.folderItemRepository = folderItemRepository;
    }

    @RabbitListener(queues = "s3-uploaded-queue")
    public void handleS3ObjectUploaded(Long objectId) {
        System.out.println("Photo upload completed for ID: " + objectId);
        folderItemRepository.findById(objectId).ifPresent(s3Object -> {
            s3Object.setUploadStatus(UploadStatus.UPLOADED);
            s3Object.setUploadTime(LocalDateTime.now());
            folderItemRepository.save(s3Object);
        });
    }

    @RabbitListener(queues = "s3-upload-failed-queue")
    public void handleS3ObjectUploadFailed(Long objectId) {
        System.out.println("Photo upload failed for ID: " + objectId);
        folderItemRepository.deleteById(objectId);
    }
}
