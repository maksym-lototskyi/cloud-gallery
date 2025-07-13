package org.example.photoservice.repository;
import org.example.photoservice.model.Photo;
import org.example.photoservice.model.UploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByUploadStatus(UploadStatus uploadStatus);
    Optional<Photo> findByParentFolderFolderUUIDAndName(UUID folderUUID, String fileName);
}
