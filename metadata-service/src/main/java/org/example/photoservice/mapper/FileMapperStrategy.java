package org.example.photoservice.mapper;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.helpers.S3LinkPresigner;
import org.example.photoservice.model.File;
import org.example.photoservice.model.FolderItem;
import org.springframework.stereotype.Component;

@Component
public class FileMapperStrategy implements S3ObjectMapperStrategy{

    @Override
    public boolean supports(FolderItem folderItem) {
        return folderItem instanceof File;
    }

    @Override
    public FolderItemResponseDto map(FolderItem folderItem) {
        if(!supports(folderItem)) {
            throw new IllegalArgumentException("Unsupported S3Object type: " + folderItem.getClass().getName());
        }
        File file = (File) folderItem;
        return FileMapper.mapToFilePreview(
                file
        );
    }
}
