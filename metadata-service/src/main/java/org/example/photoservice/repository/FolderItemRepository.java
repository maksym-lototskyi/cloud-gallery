package org.example.photoservice.repository;

import org.example.photoservice.model.Folder;
import org.example.photoservice.model.FolderItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FolderItemRepository extends ListCrudRepository<FolderItem, Long> {
    List<FolderItem> findAllByParentFolderAndUploadTimeIsNotNull(Folder folder);


    Optional<FolderItem> findByParentFolderObjectUUIDAndName(UUID folderUUID, String name);
    Optional<FolderItem> findByObjectUUID(UUID objectUUID);

    boolean existsByUserUUIDAndObjectUUID(UUID userId, UUID folderItemId);

    boolean existsByParentFolderObjectUUIDAndName(UUID parentFolderId, String folderItemName);

    @Query(value = """
        WITH RECURSIVE folder_tree AS (
            SELECT * FROM folder_item WHERE id = :rootId
            UNION ALL
            SELECT fi.* FROM folder_item fi
            INNER JOIN folder_tree ft ON fi.folder_id = ft.id
        )
        SELECT * FROM folder_tree
        """, nativeQuery = true)
    List<FolderItem> findAllDescendants(@Param("rootId") Long rootId);
}
