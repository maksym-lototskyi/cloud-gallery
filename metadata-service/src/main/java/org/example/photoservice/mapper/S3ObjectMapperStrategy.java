package org.example.photoservice.mapper;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.model.FolderItem;

public interface S3ObjectMapperStrategy {
    boolean supports(FolderItem folderItem);
    FolderItemResponseDto map(FolderItem folderItem);
}
