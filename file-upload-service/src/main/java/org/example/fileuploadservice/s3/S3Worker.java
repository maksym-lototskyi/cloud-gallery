package org.example.fileuploadservice.s3;

import org.example.fileuploadservice.events.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class S3Worker {
    private final S3Client s3Client;
    private final RabbitTemplate rabbitTemplate;

    public S3Worker(S3Client s3Client, RabbitTemplate rabbitTemplate) {
        this.s3Client = s3Client;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "s3-object-queue")
    public void processPhotoUpload(S3ObjectUploadEvent event) {
        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                .bucket(event.getBucketName())
                .key(event.getS3Key());

        if (event.getUploadType() == UploadType.FILE && event.getFileType() != null) {
            putObjectRequestBuilder.contentType(event.getFileType());
        }

        PutObjectRequest putObjectRequest = putObjectRequestBuilder.build();

        try {
            RequestBody body = (event.getFileContent() != null) ?
                    RequestBody.fromBytes(event.getFileContent()) :
                    RequestBody.empty();

            s3Client.putObject(putObjectRequest, body);
            System.out.println("Successfully uploaded object to S3: " + event.getObjectId());
            rabbitTemplate.convertAndSend("s3-upload-exchange", "upload-success-key", event.getObjectId());
        } catch (Exception e) {
            System.out.println("Failed to upload object to S3: " + e.getMessage());
            rabbitTemplate.convertAndSend("s3-upload-exchange", "upload-failed-key", event.getObjectId());
        }
    }

    @RabbitListener(queues = "folder.create.file-upload.queue")
    public void processUserRegistration(S3UploadRootFolderEvent event){
        System.out.println("Processing folder creation for user: " + event.getUserId());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(event.getBucketName())
                .key(event.getS3Key())
                .build();

        try {
            //throw new RuntimeException("Simulated failure for testing rollback");
            s3Client.putObject(putObjectRequest, RequestBody.empty());
            rabbitTemplate.convertAndSend("folder.creation.exchange",
                    "folder.create.success.metadata",
                    event.getFolderId()
                    );
            System.out.println("Successfully created root folder for user: " + event.getUserId());

        } catch (Exception e) {
            System.out.println("Failed to create root folder for user: " + event.getUserId());
            rabbitTemplate.convertAndSend("folder.rollback.exchange",
                    "folder.rollback.metadata",
                    event.getUserId()
                    );
        }
    }
}
