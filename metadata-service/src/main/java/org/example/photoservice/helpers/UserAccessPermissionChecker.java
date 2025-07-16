package org.example.photoservice.helpers;

import org.example.photoservice.repository.FolderItemRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserAccessPermissionChecker {
    private final FolderItemRepository repository;

    public UserAccessPermissionChecker(FolderItemRepository repository) {
        this.repository = repository;
    }

    public boolean hasAccessToFolder(UUID userId, UUID folderId) {
        return repository.existsByUserUUIDAndObjectUUID(userId, folderId);
    }
}
