package org.example.photoservice.repository;
import org.example.photoservice.model.Photo;
import org.example.photoservice.model.PhotoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByPhotoStatus(PhotoStatus photoStatus);
    Optional<Photo> findByS3Key(String s3Key);
}
