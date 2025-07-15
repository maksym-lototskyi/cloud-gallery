package org.example.photoservice.repository;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.S3Object;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface S3ObjectRepository extends ListCrudRepository<S3Object, Long> {
    List<S3Object> findAllByParentFolder(Folder folder);

    boolean existsByUserUUIDAndId(UUID userId, Long s3ObjectId);
}
