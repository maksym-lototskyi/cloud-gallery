package org.example.photoservice.helpers;

import org.example.photoservice.repository.FolderRepository;
import org.example.photoservice.repository.S3ObjectRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserFolderAccessPermissionChecker {
    private final FolderRepository folderRepository;

    public UserFolderAccessPermissionChecker(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public boolean hasAccessToFolder(UUID userId, UUID folderId) {
        return folderRepository.existsByUserUUIDAndFolderUUID(userId, folderId);
    }
}
