package org.example.photoservice.mapper;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.model.S3Object;

public interface S3ObjectMapperStrategy {
    boolean supports(S3Object s3Object);
    FolderItemResponseDto map(S3Object s3Object);
}
