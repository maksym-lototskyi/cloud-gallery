package org.example.photoservice.listeners;

import org.example.photoservice.repository.FolderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FolderDeleteRollbackListener {
    private final FolderRepository folderRepository;

    public FolderDeleteRollbackListener(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @RabbitListener(queues = "folder-delete-rollback-queue")
    public void handleFolderDeleteRollback(Long folderId){
        folderRepository.findById(folderId).ifPresent(folder -> {
            folder.setDeleted(false);
        });
    }
}
