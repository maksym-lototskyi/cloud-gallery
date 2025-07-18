package org.example.photoservice.service;

import org.example.photoservice.dto.FileItemMoveRequestDto;
import org.example.photoservice.dto.FolderItemRenameRequest;
import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.exception.DuplicateNameException;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.exception.RootFolderRenameException;
import org.example.photoservice.helpers.FileUtils;
import org.example.photoservice.mapper.S3ObjectMapperStrategyRegistry;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.FolderItem;
import org.example.photoservice.repository.FolderRepository;
import org.example.photoservice.repository.FolderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FolderItemService {
    private final FolderRepository folderRepository;
    private final FolderItemRepository folderItemRepository;
    private final S3ObjectMapperStrategyRegistry registry;

    public FolderItemService(FolderRepository folderRepository, FolderItemRepository folderItemRepository, S3ObjectMapperStrategyRegistry registry) {
        this.folderRepository = folderRepository;
        this.folderItemRepository = folderItemRepository;
        this.registry = registry;
    }

    @Transactional
    public FolderItemResponseDto moveObject(FileItemMoveRequestDto requestDto){
        UUID sourceFolderId = requestDto.getSourceFolderId();
        String folderItemName = requestDto.getFolderItemName();
        UUID newParentId = requestDto.getNewParentFolderId();

        if(sourceFolderId.equals(newParentId)){
            throw new IllegalArgumentException("Cannot move folder to itself");
        }

        FolderItem object = folderItemRepository.findByParentFolderObjectUUIDAndName(sourceFolderId, folderItemName)
                .orElseThrow(() -> new NotFoundException("S3 Object not found with id: " + sourceFolderId + " and name: " + folderItemName));
        Folder newParent = folderRepository.findByObjectUUID(newParentId)
                .orElseThrow(() -> new NotFoundException("New parent folder not found with id: " + newParentId));

        if(!object.getParentFolder().equals(newParent)) {
            object.setParentFolder(newParent);
            String newName = FileUtils.generateUniqueFileName(
                    object.getName(),
                    (n) -> folderItemRepository.existsByParentFolderObjectUUIDAndName(newParent.getObjectUUID(), n));
            object.setName(newName);
        }

        return registry.map(object);
    }

    @Transactional
    public FolderItemResponseDto rename(FolderItemRenameRequest renameRequest){
        FolderItem folderItem = folderItemRepository.findByObjectUUID(renameRequest.getObjectId())
                .orElseThrow(() -> new NotFoundException("Folder item with id " + renameRequest.getObjectId() + " not found"));

        Folder parent = folderItem.getParentFolder();
        if(parent == null){
            throw new RootFolderRenameException();
        }
        if(folderItemRepository.existsByParentFolderObjectUUIDAndName(parent.getObjectUUID(), renameRequest.getNewName())){
            throw new DuplicateNameException("Folder item with name " + renameRequest.getNewName() + " already exists");
        }

        folderItem.setName(renameRequest.getNewName());
        return registry.map(folderItem);
    }
}
