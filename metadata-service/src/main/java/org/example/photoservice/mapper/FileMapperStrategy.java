package org.example.photoservice.mapper;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.helpers.S3LinkPresigner;
import org.example.photoservice.model.File;
import org.example.photoservice.model.S3Object;
import org.springframework.stereotype.Component;

@Component
public class FileMapperStrategy implements S3ObjectMapperStrategy{
    private final S3LinkPresigner presigner;

    public FileMapperStrategy(S3LinkPresigner presigner) {
        this.presigner = presigner;
    }

    @Override
    public boolean supports(S3Object s3Object) {
        return s3Object instanceof File;
    }

    @Override
    public FolderItemResponseDto map(S3Object s3Object) {
        if(!supports(s3Object)) {
            throw new IllegalArgumentException("Unsupported S3Object type: " + s3Object.getClass().getName());
        }
        File file = (File) s3Object;
        return FileMapper.mapToPhotoResponse(
                file,
                presigner.generateGetPresignURI(file.getS3Bucket(), file.getS3Key())
        );
    }
}
