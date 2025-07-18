package org.example.photoservice.repository;

import org.example.photoservice.model.Folder;
import org.example.photoservice.model.FolderItem;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FolderItemRepository extends ListCrudRepository<FolderItem, Long> {
    List<FolderItem> findAllByParentFolderAndUploadTimeIsNotNull(Folder folder);


    Optional<FolderItem> findByParentFolderObjectUUIDAndName(UUID folderUUID, String name);
    Optional<FolderItem> findByObjectUUID(UUID objectUUID);

    boolean existsByUserUUIDAndObjectUUID(UUID userId, UUID folderItemId);

    boolean existsByParentFolderObjectUUIDAndName(UUID parentFolderId, String folderItemName);
}
