package org.example.fileuploadservice.listeners;

import org.example.fileuploadservice.events.FolderContentDeleteEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

import java.util.List;

@Component
public class DeleteFolderContentListener {
    private final S3Client s3Client;
    private final RabbitTemplate rabbitTemplate;

    public DeleteFolderContentListener(S3Client s3Client, RabbitTemplate rabbitTemplate) {
        this.s3Client = s3Client;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "folder-content-delete-queue")
    public void deleteFolderContent(FolderContentDeleteEvent deleteEvent) {
        String bucketName = deleteEvent.getBucketName();
        List<String> keys = deleteEvent.getS3Keys();

        if (keys.isEmpty()) return;

        List<ObjectIdentifier> toDelete = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        Delete delete = Delete.builder()
                .objects(toDelete)
                .build();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete)
                .build();

        try{
            s3Client.deleteObjects(request);
        } catch (Exception e) {
            System.err.println("Error deleting objects from S3: " + e.getMessage());
            rabbitTemplate.convertAndSend("folder.delete.rollback.exchange",
                    "folder.delete.rollback.key",
                    deleteEvent.getFolderId());
        }
    }
}
