package org.example.photoservice.service;

import org.example.photoservice.dto.request.FileItemMoveRequestDto;
import org.example.photoservice.dto.request.FolderItemRenameRequest;
import org.example.photoservice.dto.response.FilePreviewResponseDto;
import org.example.photoservice.dto.response.FolderItemResponseDto;
import org.example.photoservice.exception.DuplicateNameException;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.exception.RootFolderMutationException;
import org.example.photoservice.helpers.FileUtils;
import org.example.photoservice.helpers.FolderDeletionChecker;
import org.example.photoservice.mapper.S3ObjectMapperStrategyRegistry;
import org.example.photoservice.model.File;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.FolderItem;
import org.example.photoservice.repository.FolderItemRepository;
import org.example.photoservice.repository.FolderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolderItemServiceTest {

    @Mock
    private FolderRepository folderRepository;
    @Mock
    private FolderItemRepository folderItemRepository;
    @Mock
    private S3ObjectMapperStrategyRegistry registry;

    @InjectMocks
    private FolderItemService folderItemService;

    @Test
    void testMoveObjectChangesParentAndRenamesIfDifferentFolder() {
        UUID sourceFolderId = UUID.randomUUID();
        UUID newParentId = UUID.randomUUID();
        FileItemMoveRequestDto requestDto = FileItemMoveRequestDto.builder()
                .newParentFolderId(newParentId)
                .sourceFolderId(sourceFolderId)
                .build();
        requestDto.setSourceFolderId(sourceFolderId);
        requestDto.setFolderItemName("file.txt");
        requestDto.setNewParentFolderId(newParentId);

        Folder oldParent = Folder.builder().objectUUID(sourceFolderId).build();
        Folder newParent = Folder.builder().objectUUID(newParentId).build();
        FolderItem item = File.builder()
                .name("file.txt")
                .parentFolder(oldParent)
                .build();
        FolderItemResponseDto expectedDto = FilePreviewResponseDto.builder().name("new-file.txt").build();

        when(folderItemRepository.findByParentFolderObjectUUIDAndName(sourceFolderId, "file.txt"))
                .thenReturn(Optional.of(item));
        when(folderRepository.findByObjectUUID(newParentId)).thenReturn(Optional.of(newParent));

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class);
             MockedStatic<FileUtils> fileUtilsMock = Mockito.mockStatic(FileUtils.class)) {

            checkerMock.when(() -> FolderDeletionChecker.isAccessible(item)).thenReturn(true);
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(newParent)).thenReturn(true);
            fileUtilsMock.when(() -> FileUtils.generateUniqueFileName(eq("file.txt"), any())).thenReturn("new-file.txt");
            when(registry.map(item)).thenReturn(expectedDto);

            FolderItemResponseDto result = folderItemService.moveObject(requestDto);

            assertEquals("new-file.txt", item.getName());
            assertSame(newParent, item.getParentFolder());
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void testMoveObjectThrowsIfItemNotFound() {
        UUID sourceFolderId = UUID.randomUUID();
        FileItemMoveRequestDto requestDto = FileItemMoveRequestDto.builder().build();
        requestDto.setSourceFolderId(sourceFolderId);
        requestDto.setFolderItemName("missing.txt");

        when(folderItemRepository.findByParentFolderObjectUUIDAndName(sourceFolderId, "missing.txt"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> folderItemService.moveObject(requestDto));
    }

    @Test
    void testMoveObjectThrowsIfItemInaccessible() {
        UUID sourceFolderId = UUID.randomUUID();
        FileItemMoveRequestDto requestDto = FileItemMoveRequestDto.builder().build();
        requestDto.setSourceFolderId(sourceFolderId);
        requestDto.setFolderItemName("file.txt");

        FolderItem item = File.builder().name("file.txt").build();
        when(folderItemRepository.findByParentFolderObjectUUIDAndName(sourceFolderId, "file.txt"))
                .thenReturn(Optional.of(item));

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(item)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> folderItemService.moveObject(requestDto));
        }
    }

    @Test
    void testMoveObjectThrowsIfNewParentNotFound() {
        UUID sourceFolderId = UUID.randomUUID();
        UUID newParentId = UUID.randomUUID();
        FileItemMoveRequestDto requestDto = FileItemMoveRequestDto.builder().build();
        requestDto.setSourceFolderId(sourceFolderId);
        requestDto.setFolderItemName("file.txt");
        requestDto.setNewParentFolderId(newParentId);

        FolderItem item = File.builder().name("file.txt").build();
        when(folderItemRepository.findByParentFolderObjectUUIDAndName(sourceFolderId, "file.txt"))
                .thenReturn(Optional.of(item));
        when(folderRepository.findByObjectUUID(newParentId)).thenReturn(Optional.empty());

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(item)).thenReturn(true);

            assertThrows(NotFoundException.class, () -> folderItemService.moveObject(requestDto));
        }
    }

    @Test
    void testMoveObjectDoesNotRenameIfSameParentFolder() {
        UUID folderId = UUID.randomUUID();
        FileItemMoveRequestDto requestDto = FileItemMoveRequestDto.builder().build();
        requestDto.setSourceFolderId(folderId);
        requestDto.setFolderItemName("file.txt");
        requestDto.setNewParentFolderId(folderId);

        Folder parent = Folder.builder().objectUUID(folderId).build();
        FolderItem item = File.builder().name("file.txt").parentFolder(parent).build();
        FolderItemResponseDto dto = FilePreviewResponseDto.builder().name("file.txt").build();

        when(folderItemRepository.findByParentFolderObjectUUIDAndName(folderId, "file.txt")).thenReturn(Optional.of(item));
        when(folderRepository.findByObjectUUID(folderId)).thenReturn(Optional.of(parent));

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(item)).thenReturn(true);
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(parent)).thenReturn(true);
            when(registry.map(item)).thenReturn(dto);

            FolderItemResponseDto result = folderItemService.moveObject(requestDto);

            assertEquals("file.txt", item.getName());
            assertEquals(dto, result);
        }
    }

    @Test
    void testRenameChangesNameIfValid() {
        UUID objectId = UUID.randomUUID();
        Folder parent = Folder.builder().objectUUID(UUID.randomUUID()).build();
        FolderItem item = File.builder().name("old.txt").parentFolder(parent).build();
        FolderItemRenameRequest renameRequest = new FolderItemRenameRequest();
        renameRequest.setObjectId(objectId);
        renameRequest.setNewName("new.txt");

        when(folderItemRepository.findByObjectUUID(objectId)).thenReturn(Optional.of(item));
        when(folderItemRepository.existsByParentFolderObjectUUIDAndName(parent.getObjectUUID(), "new.txt")).thenReturn(false);
        FolderItemResponseDto dto = FilePreviewResponseDto.builder().name("new.txt").build();

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(item)).thenReturn(true);
            when(registry.map(item)).thenReturn(dto);

            FolderItemResponseDto result = folderItemService.rename(renameRequest);

            assertEquals("new.txt", item.getName());
            assertEquals(dto, result);
        }
    }

    @Test
    void testRenameThrowsIfItemNotFound() {
        UUID objectId = UUID.randomUUID();
        FolderItemRenameRequest request = new FolderItemRenameRequest();
        request.setObjectId(objectId);

        when(folderItemRepository.findByObjectUUID(objectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> folderItemService.rename(request));
    }

    @Test
    void testRenameThrowsIfItemInaccessible() {
        UUID objectId = UUID.randomUUID();
        FolderItemRenameRequest request = new FolderItemRenameRequest();
        request.setObjectId(objectId);

        FolderItem item = File.builder().build();
        when(folderItemRepository.findByObjectUUID(objectId)).thenReturn(Optional.of(item));

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(item)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> folderItemService.rename(request));
        }
    }

    @Test
    void testRenameThrowsIfRootFolderItem() {
        UUID objectId = UUID.randomUUID();
        FolderItem item = File.builder().parentFolder(null).build();
        FolderItemRenameRequest request = new FolderItemRenameRequest();
        request.setObjectId(objectId);
        request.setNewName("new.txt");

        when(folderItemRepository.findByObjectUUID(objectId)).thenReturn(Optional.of(item));

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(item)).thenReturn(true);

            assertThrows(RootFolderMutationException.class, () -> folderItemService.rename(request));
        }
    }

    @Test
    void testRenameThrowsIfDuplicateNameExists() {
        UUID objectId = UUID.randomUUID();
        Folder parent = Folder.builder().objectUUID(UUID.randomUUID()).build();
        FolderItem item = File.builder().name("old.txt").parentFolder(parent).build();
        FolderItemRenameRequest request = new FolderItemRenameRequest();
        request.setObjectId(objectId);
        request.setNewName("duplicate.txt");

        when(folderItemRepository.findByObjectUUID(objectId)).thenReturn(Optional.of(item));
        when(folderItemRepository.existsByParentFolderObjectUUIDAndName(parent.getObjectUUID(), "duplicate.txt")).thenReturn(true);

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {
            checkerMock.when(() -> FolderDeletionChecker.isAccessible(item)).thenReturn(true);

            assertThrows(DuplicateNameException.class, () -> folderItemService.rename(request));
        }
    }
}
