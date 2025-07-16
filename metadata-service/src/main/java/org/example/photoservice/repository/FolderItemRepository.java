package org.example.photoservice.repository;

import org.example.photoservice.model.Folder;
import org.example.photoservice.model.FolderItem;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FolderItemRepository extends ListCrudRepository<FolderItem, Long> {
    List<FolderItem> findAllByParentFolder(Folder folder);


    Optional<FolderItem> findByParentFolderObjectUUIDAndName(UUID folderUUID, String name);

    boolean existsByUserUUIDAndObjectUUID(UUID userId, UUID folderItemId);

    boolean existsByParentFolderObjectUUIDAndName(UUID parentFolderId, String folderItemName);
}
