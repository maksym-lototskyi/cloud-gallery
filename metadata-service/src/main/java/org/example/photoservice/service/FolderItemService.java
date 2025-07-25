package org.example.photoservice.service;

import org.example.photoservice.dto.request.FileItemMoveRequestDto;
import org.example.photoservice.dto.request.FolderItemRenameRequest;
import org.example.photoservice.dto.response.FolderItemResponseDto;
import org.example.photoservice.exception.DuplicateNameException;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.exception.RootFolderMutationException;
import org.example.photoservice.helpers.FileUtils;
import org.example.photoservice.helpers.FolderDeletionChecker;
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

        FolderItem item = folderItemRepository.findByParentFolderObjectUUIDAndName(sourceFolderId, folderItemName)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + sourceFolderId + " and name: " + folderItemName));

        if(!FolderDeletionChecker.isAccessible(item)){
            throw new NotFoundException("Item not found with id: " + sourceFolderId + " and name: " + folderItemName);
        }
        Folder newParent = folderRepository.findByObjectUUID(newParentId)
                .orElseThrow(() -> new NotFoundException("Folder not found with id: " + newParentId));

        if (!FolderDeletionChecker.isAccessible(newParent)) {
            throw new NotFoundException("Folder not found with id: " + newParentId);
        }

        if(!item.getParentFolder().equals(newParent)) {
            item.setParentFolder(newParent);
            String newName = FileUtils.generateUniqueFileName(
                    item.getName(),
                    (n) -> folderItemRepository.existsByParentFolderObjectUUIDAndName(newParent.getObjectUUID(), n));
            item.setName(newName);
        }

        return registry.map(item);
    }

    @Transactional
    public FolderItemResponseDto rename(FolderItemRenameRequest renameRequest){
        FolderItem folderItem = folderItemRepository.findByObjectUUID(renameRequest.getObjectId())
                .orElseThrow(() -> new NotFoundException("Folder item with id " + renameRequest.getObjectId() + " not found"));

        if(!FolderDeletionChecker.isAccessible(folderItem)){
            throw new NotFoundException("Folder item with id " + renameRequest.getObjectId() + " not found");
        }

        Folder parent = folderItem.getParentFolder();
        if(parent == null){
            throw new RootFolderMutationException();
        }
        if(folderItemRepository.existsByParentFolderObjectUUIDAndName(parent.getObjectUUID(), renameRequest.getNewName())){
            throw new DuplicateNameException("Folder item with name " + renameRequest.getNewName() + " already exists");
        }

        folderItem.setName(renameRequest.getNewName());
        return registry.map(folderItem);
    }
}
