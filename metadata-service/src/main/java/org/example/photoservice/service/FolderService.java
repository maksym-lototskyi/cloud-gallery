package org.example.photoservice.service;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.FolderContentDto;
import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.dto.FolderRequestDto;
import org.example.photoservice.dto.FolderResponseDto;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.mapper.FolderMapper;
import org.example.photoservice.mapper.S3ObjectMapperStrategyRegistry;
import org.example.photoservice.model.Folder;
import org.example.photoservice.repository.FolderRepository;
import org.example.photoservice.repository.S3ObjectRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final S3Properties s3Properties;
    private final RabbitTemplate rabbitTemplate;
    private final S3ObjectRepository s3ObjectRepository;
    private final S3ObjectMapperStrategyRegistry registry;

    public FolderService(FolderRepository folderRepository, S3Properties s3Properties, RabbitTemplate rabbitTemplate, S3ObjectRepository s3ObjectRepository, S3ObjectMapperStrategyRegistry registry) {
        this.folderRepository = folderRepository;
        this.s3Properties = s3Properties;
        this.rabbitTemplate = rabbitTemplate;
        this.s3ObjectRepository = s3ObjectRepository;
        this.registry = registry;
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
        rabbitTemplate.convertAndSend("s3-exchange", "s3-upload-key", FolderMapper.mapToEvent(folder));

        return FolderMapper.mapToFolderResponseDto(folder);
    }

    public FolderResponseDto getRootFolder(UUID userId) {
        Folder rootFolder = folderRepository.findByUserUUIDAndParentFolderIsNull(userId)
                .orElseThrow(() -> new NotFoundException("Root folder not found for user with id: " + userId));

        return FolderMapper.mapToFolderResponseDto(rootFolder);
    }

    public FolderContentDto getFolderContent(UUID folderId) {
        Folder folder = folderRepository.findByFolderUUID(folderId)
                .orElseThrow(() -> new NotFoundException("Folder not found with id: " + folderId));

        return FolderMapper.mapToFolderContentDto(folder, s3ObjectRepository.findAllByParentFolder(folder)
                .stream()
                .map(registry::map)
                .collect(Collectors.toList()));
    }

    @Transactional
    public FolderResponseDto moveFolder(UUID folderUUID, UUID newParentId){
        if(folderUUID.equals(newParentId)){
            throw new IllegalArgumentException("Cannot move folder to itself");
        }

        Folder folder = folderRepository.findByFolderUUID(folderUUID)
                .orElseThrow(() -> new NotFoundException("Folder not found with id: " + folderUUID));
        Folder newParent = folderRepository.findByFolderUUID(newParentId)
                .orElseThrow(() -> new NotFoundException("New parent folder not found with id: " + newParentId));

        if(!folder.getParentFolder().equals(newParent)){
            folder.setParentFolder(newParent);
        }

        return FolderMapper.mapToFolderResponseDto(folder);
    }

    @Transactional
    public void deleteFolder(UUID folderUUID) {
        Folder folder = folderRepository.findByFolderUUID(folderUUID)
                .orElseThrow(() -> new NotFoundException("Folder not found with id: " + folderUUID));

        folderRepository.delete(folder);
    }

}
