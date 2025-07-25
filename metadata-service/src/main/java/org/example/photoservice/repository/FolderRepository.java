package org.example.photoservice.repository;

import org.example.photoservice.model.Folder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FolderRepository extends ListCrudRepository<Folder, Long> {
    boolean existsByUserUUIDAndParentFolderIsNull(UUID userUUID);

    Optional<Folder> findByObjectUUID(UUID folderUUID);
    Optional<Folder> findByUserUUIDAndParentFolderIsNull(UUID userUUID);

    boolean existsByUserUUID(UUID userId);

    @Query(value = """
        WITH RECURSIVE folder_tree AS (
            SELECT id FROM folder_item WHERE id = :folderId AND object_type = 'FOLDER'
            UNION ALL
            SELECT fi.id FROM folder_item fi
            INNER JOIN folder_tree ft ON fi.folder_id = ft.id
            WHERE fi.object_type = 'FOLDER'
        )
        SELECT id FROM folder_tree
        """, nativeQuery = true)
    List<Long> findAllDescendantFolderIds(@Param("folderId") Long folderId);
}
