package org.example.photoservice.service;

import org.example.photoservice.S3Properties;
import org.example.photoservice.repository.FolderRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final S3Properties s3Properties;
    private final S3Client s3Client;

    public FolderService(FolderRepository folderRepository, S3Properties s3Properties, S3Client s3Client) {
        this.folderRepository = folderRepository;
        this.s3Properties = s3Properties;
        this.s3Client = s3Client;
    }
}
