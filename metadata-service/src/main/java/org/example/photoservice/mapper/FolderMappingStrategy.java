package org.example.photoservice.mapper;

import org.example.photoservice.dto.response.FolderItemResponseDto;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.FolderItem;
import org.springframework.stereotype.Component;

@Component
public class FolderMappingStrategy implements S3ObjectMapperStrategy{
    @Override
    public boolean supports(FolderItem folderItem) {
        return folderItem instanceof Folder;
    }

    @Override
    public FolderItemResponseDto map(FolderItem folderItem) {
        if (!supports(folderItem)) {
            throw new IllegalArgumentException("Invalid S3Object type for mapping to FolderItemResponseDto");
        }
        Folder folder = (Folder) folderItem;
        return FolderMapper.mapToFolderResponseDto(folder);
    }
}
