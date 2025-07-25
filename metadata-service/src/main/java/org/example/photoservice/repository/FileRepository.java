package org.example.photoservice.repository;
import org.example.photoservice.model.File;
import org.example.photoservice.model.UploadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, Long> {
    Page<File> findAllByUploadStatusAndUserUUID(UploadStatus uploadStatus, UUID userId, Pageable pageable);

    Optional<File> findByObjectUUID(UUID objectUUID);

    boolean existsByParentFolderObjectUUIDAndName(UUID folderId, String originalFilename);

    @Query("""
        SELECT f.objectUUID FROM File f WHERE f.parentFolder.id IN :folderIds
    """)
    List<UUID> findObjectUUIDsByParentFolderIds(List<Long> folderIds);
}
