package org.example.photoservice.mapper;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.S3Object;
import org.springframework.stereotype.Component;

@Component
public class FolderMappingStrategy implements S3ObjectMapperStrategy{
    @Override
    public boolean supports(S3Object s3Object) {
        return s3Object instanceof Folder;
    }

    @Override
    public FolderItemResponseDto map(S3Object s3Object) {
        if (!supports(s3Object)) {
            throw new IllegalArgumentException("Invalid S3Object type for mapping to FolderItemResponseDto");
        }
        Folder folder = (Folder) s3Object;
        return FolderMapper.mapToFolderResponseDto(folder);
    }
}
