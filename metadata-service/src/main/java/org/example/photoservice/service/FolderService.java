package org.example.photoservice.service;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.FolderRequestDto;
import org.example.photoservice.dto.FolderResponseDto;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.mapper.FolderMapper;
import org.example.photoservice.model.Folder;
import org.example.photoservice.repository.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final S3Properties s3Properties;

    public FolderService(FolderRepository folderRepository, S3Properties s3Properties) {
        this.folderRepository = folderRepository;
        this.s3Properties = s3Properties;
    }

    public FolderResponseDto createFolder(FolderRequestDto folderRequestDto, UUID userId) {
        Folder parent = folderRepository.findByFolderUUID(folderRequestDto.getParentFolderId())
                .orElseThrow(() -> new NotFoundException("Parent folder not found with id: " + folderRequestDto.getParentFolderId()));

        Folder folder = FolderMapper.mapToFolder(
                folderRequestDto.getName(),
                parent,
                userId
                ,s3Properties
        );
        folder = folderRepository.save(folder);
        return FolderMapper.mapToFolderResponseDto(folder);
    }
}
