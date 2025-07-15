package org.example.photoservice.repository;

import org.example.photoservice.model.Folder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface FolderRepository extends ListCrudRepository<Folder, Long> {
    boolean existsByUserUUIDAndParentFolderIsNull(UUID userUUID);

    Optional<Folder> findByFolderUUID(UUID folderUUID);
    Optional<Folder> findByUserUUIDAndParentFolderIsNull(UUID userUUID);

    boolean existsByUserUUID(UUID userId);

    void deleteByUserUUID(UUID userId);
}
