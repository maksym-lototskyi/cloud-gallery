package org.example.photoservice.repository;
import org.example.photoservice.model.File;
import org.example.photoservice.model.UploadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, Long> {
    Page<File> findAllByUploadStatusAndUserUUID(UploadStatus uploadStatus, UUID userId, Pageable pageable);

    Optional<File> findByObjectUUID(UUID objectUUID);

    boolean existsByParentFolderObjectUUIDAndName(UUID folderId, String originalFilename);
}
