package org.example.photoservice.repository;
import org.example.photoservice.model.File;
import org.example.photoservice.model.UploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByUploadStatus(UploadStatus uploadStatus);
    Optional<File> findByParentFolderFolderUUIDAndName(UUID folderUUID, String fileName);
}
