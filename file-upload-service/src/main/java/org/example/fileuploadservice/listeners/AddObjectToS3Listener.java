package org.example.fileuploadservice.listeners;

import org.example.fileuploadservice.events.S3ObjectUploadEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class AddObjectToS3Listener {
    private final S3Client s3Client;
    private final RabbitTemplate rabbitTemplate;

    public AddObjectToS3Listener(S3Client s3Client, RabbitTemplate rabbitTemplate) {
        this.s3Client = s3Client;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "s3-object-queue")
    public void processPhotoUpload(S3ObjectUploadEvent event) {
        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                .bucket(event.getBucketName())
                .key(event.getS3Key())
                .contentType(event.getFileType());

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
}
