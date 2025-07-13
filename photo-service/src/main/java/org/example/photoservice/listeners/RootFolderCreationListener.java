package org.example.photoservice.listeners;

import org.example.photoservice.S3Properties;
import org.example.photoservice.events.S3UploadRootFolderEvent;
import org.example.photoservice.events.S3UploadRootFolderFailedEvent;
import org.example.photoservice.mapper.FolderMapper;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.UploadStatus;
import org.example.photoservice.repository.FolderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RootFolderCreationListener {
    private final FolderRepository folderRepository;
    private final S3Properties s3Properties;
    private final RabbitTemplate rabbitTemplate;

    public RootFolderCreationListener(FolderRepository folderRepository, S3Properties s3Properties, RabbitTemplate rabbitTemplate) {
        this.folderRepository = folderRepository;
        this.s3Properties = s3Properties;
        this.rabbitTemplate = rabbitTemplate;
    }


    @RabbitListener(queues = "folder.create.metadata.queue")
    public void createUserFolder(UUID userId) {
        System.out.println("Received user ID: " + userId);
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (folderRepository.existsByUserUUID(userId)) {
            return;
        }

        Folder rootFolder = createRootFolder(userId);

        try {
            folderRepository.save(rootFolder);
            System.out.println("Root folder created for user ID: " + userId);
        }
        catch (Exception e) {
            System.out.println("Error creating root folder for user ID: " + userId);
            rabbitTemplate.convertAndSend("folder.rollback.exchange",
                    "folder.rollback.user-metadata",
                    rootFolder.getUserUUID());
            return;
        }
        rabbitTemplate.convertAndSend("folder.creation.exchange",
                "folder.create.file-upload.key",
                S3UploadRootFolderEvent
                        .builder()
                        .path(rootFolder.getFullPath())
                        .bucketName(rootFolder.getS3Bucket())
                        .folderId(rootFolder.getId())
                        .userId(rootFolder.getUserUUID())
                        .build()
        );
        System.out.println("Sent S3 upload event for user ID: " + userId);
    }

    @RabbitListener(queues = "folder.rollback.metadata.queue")
    @Transactional
    public void rollbackUserFolder(UUID userId){
        System.out.println("Received rollback event for user ID: " + userId);
        folderRepository.deleteByUserUUID(userId);
    }

    private Folder createRootFolder(UUID userId) {
        return Folder.builder()
                .folderUUID(UUID.randomUUID())
                .userUUID(userId)
                .name("Root")
                .parentFolder(null)
                .fullPath(userId + "/")
                .uploadStatus(UploadStatus.PENDING)
                .s3Bucket(s3Properties.getBucketName())
                .build();
    }
}
