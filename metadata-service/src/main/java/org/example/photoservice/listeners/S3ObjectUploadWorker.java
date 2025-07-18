package org.example.photoservice.listeners;

import org.example.photoservice.model.UploadStatus;
import org.example.photoservice.repository.FileRepository;
import org.example.photoservice.repository.FolderItemRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class S3ObjectUploadWorker {
    private final FileRepository fileRepository;

    public S3ObjectUploadWorker(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @RabbitListener(queues = "s3-uploaded-queue")
    public void handleS3ObjectUploaded(Long objectId) {
        System.out.println("Photo upload completed for ID: " + objectId);
        fileRepository.findById(objectId).ifPresent(file -> {
            file.setUploadStatus(UploadStatus.UPLOADED);
            file.setUploadTime(LocalDateTime.now());
            fileRepository.save(file);
        });
    }

    @RabbitListener(queues = "s3-upload-failed-queue")
    public void handleS3ObjectUploadFailed(Long objectId) {
        System.out.println("Photo upload failed for ID: " + objectId);
        fileRepository.deleteById(objectId);
    }
}
