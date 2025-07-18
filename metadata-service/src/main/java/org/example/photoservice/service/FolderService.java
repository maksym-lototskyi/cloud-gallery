package org.example.photoservice.service;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.FolderContentResponseDto;
import org.example.photoservice.dto.FolderRequestDto;
import org.example.photoservice.dto.FolderResponseDto;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.mapper.FolderMapper;
import org.example.photoservice.mapper.S3ObjectMapperStrategyRegistry;
import org.example.photoservice.model.Folder;
import org.example.photoservice.repository.FolderRepository;
import org.example.photoservice.repository.FolderItemRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final S3Properties s3Properties;
    private final RabbitTemplate rabbitTemplate;
    private final FolderItemRepository folderItemRepository;
    private final S3ObjectMapperStrategyRegistry registry;

    public FolderService(FolderRepository folderRepository, S3Properties s3Properties, RabbitTemplate rabbitTemplate, FolderItemRepository folderItemRepository, S3ObjectMapperStrategyRegistry registry) {
        this.folderRepository = folderRepository;
        this.s3Properties = s3Properties;
        this.rabbitTemplate = rabbitTemplate;
        this.folderItemRepository = folderItemRepository;
        this.registry = registry;
    }

    public FolderResponseDto createFolder(FolderRequestDto folderRequestDto, UUID userId) {
        Folder parent = folderRepository.findByObjectUUID(folderRequestDto.getNewParentFolderId())
                .orElseThrow(() -> new NotFoundException("Parent folder not found with id: " + folderRequestDto.getNewParentFolderId()));

        Folder folder = FolderMapper.mapToFolder(
                folderRequestDto.getName(),
                parent,
                userId
                ,s3Properties
        );
        folder = folderRepository.save(folder);

        return FolderMapper.mapToFolderResponseDto(folder);
    }

    public FolderResponseDto getRootFolder(UUID userId) {
        Folder rootFolder = folderRepository.findByUserUUIDAndParentFolderIsNull(userId)
                .orElseThrow(() -> new NotFoundException("Root folder not found for user with id: " + userId));

        return FolderMapper.mapToFolderResponseDto(rootFolder);
    }

    public FolderContentResponseDto getFolderContent(UUID folderId) {
        Folder folder = folderRepository.findByObjectUUID(folderId)
                .orElseThrow(() -> new NotFoundException("Folder not found with id: " + folderId));

        return FolderMapper.mapToFolderContentDto(folder, folderItemRepository.findAllByParentFolderAndUploadTimeIsNotNull(folder)
                .stream()
                .map(registry::map)
                .collect(Collectors.toList()));
    }

    @Transactional
    public void deleteFolder(UUID folderUUID) {
        Folder folder = folderRepository.findByObjectUUID(folderUUID)
                .orElseThrow(() -> new NotFoundException("Folder not found with id: " + folderUUID));

        folderRepository.delete(folder);
    }

}
