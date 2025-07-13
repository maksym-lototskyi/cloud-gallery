package org.example.photoservice.repository;

import org.example.photoservice.model.S3Object;
import org.springframework.data.repository.ListCrudRepository;

public interface S3ObjectRepository extends ListCrudRepository<S3Object, Long> {
}
