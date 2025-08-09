package org.example.photoservice.service;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.request.FolderRequestDto;
import org.example.photoservice.dto.response.FolderContentResponseDto;
import org.example.photoservice.dto.response.FolderItemResponseDto;
import org.example.photoservice.dto.response.FolderResponseDto;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.exception.RootFolderMutationException;
import org.example.photoservice.helpers.FolderDeletionChecker;
import org.example.photoservice.mapper.FolderMapper;
import org.example.photoservice.mapper.S3ObjectMapperStrategyRegistry;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.FolderItem;
import org.example.photoservice.repository.FileRepository;
import org.example.photoservice.repository.FolderItemRepository;
import org.example.photoservice.repository.FolderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {
    @Mock
    private FolderRepository folderRepository;
    @Mock
    private S3Properties s3Properties;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private FolderItemRepository folderItemRepository;
    @Mock
    private S3ObjectMapperStrategyRegistry registry;
    @Mock
    private FileRepository fileRepository;
    @InjectMocks
    private FolderService folderService;

    @Test
    void testCreateFolderReturnsDtoWhenParentExistsAndAccessible() {
        UUID parentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FolderRequestDto request = new FolderRequestDto();
        request.setName("NewFolder");
        request.setParentFolderId(parentId);

        Folder parentFolder = Folder.builder().objectUUID(parentId).build();
        Folder savedFolder = Folder.builder().objectUUID(UUID.randomUUID()).build();
        FolderResponseDto responseDto = FolderResponseDto.builder().fileItemId(savedFolder.getObjectUUID()).build();

        when(folderRepository.findByObjectUUID(parentId)).thenReturn(Optional.of(parentFolder));
        when(folderRepository.save(any(Folder.class))).thenReturn(savedFolder);

        try (MockedStatic<FolderDeletionChecker> checkerMock = Mockito.mockStatic(FolderDeletionChecker.class);
             MockedStatic<FolderMapper> mapperMock = Mockito.mockStatic(FolderMapper.class)) {

            checkerMock.when(() -> FolderDeletionChecker.isAccessible(parentFolder)).thenReturn(true);
            mapperMock.when(() -> FolderMapper.mapToFolder("NewFolder", parentFolder, userId, s3Properties)).thenReturn(savedFolder);
            mapperMock.when(() -> FolderMapper.mapToFolderResponseDto(savedFolder)).thenReturn(responseDto);

            FolderResponseDto result = folderService.createFolder(request, userId);

            assertNotNull(result);
            assertEquals(savedFolder.getObjectUUID(), result.getFileItemId());
            verify(folderRepository).save(savedFolder);
        }
    }

    @Test
    void testCreateFolderThrowsWhenParentNotFound() {
        UUID parentId = UUID.randomUUID();
        FolderRequestDto request = new FolderRequestDto();
        request.setParentFolderId(parentId);

        when(folderRepository.findByObjectUUID(parentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> folderService.createFolder(request, UUID.randomUUID()));
    }

    @Test
    public void testThatGetRootFolderReturnsRootFolderIfExists(){
        UUID userId = UUID.randomUUID();
        var rootFolder = Folder.builder()
                .id(1L)
                .parentFolder(null)
                .name("Root")
                .userUUID(userId)
                .objectUUID(UUID.randomUUID())
                .uploadTime(LocalDateTime.now())
                .build();

        when(folderRepository.findByUserUUIDAndParentFolderIsNull(userId)).thenReturn(Optional.of(rootFolder));

        var result = folderService.getRootFolder(userId);

        assertNotNull(result);
        verify(folderRepository, times(1)).findByUserUUIDAndParentFolderIsNull(userId);
        assertEquals(rootFolder.getObjectUUID(), result.getFileItemId());
    }

    @Test
    public void testThatGetRootFolderThrowsExceptionIfFolderDoesNotExist(){
        UUID userId = UUID.randomUUID();

        when(folderRepository.findByUserUUIDAndParentFolderIsNull(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            folderService.getRootFolder(userId);
        });

        assertEquals("Root folder not found for user with id: " + userId, exception.getMessage());
        verify(folderRepository, times(1)).findByUserUUIDAndParentFolderIsNull(userId);
    }

    @Test
    void testGetFolderContentReturnsMappedDto() {
        UUID folderId = UUID.randomUUID();
        Folder folder = Folder.builder().objectUUID(folderId).build();
        List<FolderItem> items = List.of(mock(FolderItem.class));
        FolderItemResponseDto mappedObject = FolderResponseDto.builder()
                .fileItemId(UUID.randomUUID())
                .build();
        FolderContentResponseDto dto = FolderContentResponseDto.builder()
                .folderItems(List.of(mappedObject))
                .build();

        when(folderRepository.findByObjectUUID(folderId)).thenReturn(Optional.of(folder));
        when(folderItemRepository.findAllByParentFolderAndUploadTimeIsNotNull(folder)).thenReturn(items);
        when(registry.map(any())).thenReturn(mappedObject);

        try (MockedStatic<FolderMapper> mapperMock = Mockito.mockStatic(FolderMapper.class)) {
            mapperMock.when(() -> FolderMapper.mapToFolderContentDto(eq(folder), anyList())).thenReturn(dto);

            FolderContentResponseDto result = folderService.getFolderContent(folderId);

            assertNotNull(result);
            verify(folderItemRepository).findAllByParentFolderAndUploadTimeIsNotNull(folder);
        }
    }

    @Test
    void testGetFolderContentThrowsIfFolderNotFound() {
        UUID folderId = UUID.randomUUID();
        when(folderRepository.findByObjectUUID(folderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> folderService.getFolderContent(folderId));
    }

    @Test
    void testDeleteFolderMarksDeletedAndSendsS3Keys() {
        UUID folderUUID = UUID.randomUUID();
        Folder folder = Folder.builder()
                .id(1L)
                .objectUUID(folderUUID)
                .parentFolder(Folder.builder().id(99L).build()) // not root
                .build();
        List<Long> descendantIds = List.of(1L, 2L);
        List<UUID> s3Keys = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(folderRepository.findByObjectUUID(folderUUID)).thenReturn(Optional.of(folder));
        when(folderRepository.findAllDescendantFolderIds(folder.getId())).thenReturn(descendantIds);
        when(fileRepository.findObjectUUIDsByParentFolderIds(descendantIds)).thenReturn(s3Keys);

        folderService.deleteFolder(folderUUID);

        assertTrue(folder.isDeleted());
        verify(rabbitTemplate).convertAndSend("folder.delete.exchange", "folder.delete.key", s3Keys);
    }

    @Test
    void testDeleteFolderThrowsIfRootFolder() {
        UUID folderUUID = UUID.randomUUID();
        Folder folder = Folder.builder().objectUUID(folderUUID).parentFolder(null).build();

        when(folderRepository.findByObjectUUID(folderUUID)).thenReturn(Optional.of(folder));

        assertThrows(RootFolderMutationException.class, () -> folderService.deleteFolder(folderUUID));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testDeleteFolderThrowsIfNotFound() {
        UUID folderUUID = UUID.randomUUID();
        when(folderRepository.findByObjectUUID(folderUUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> folderService.deleteFolder(folderUUID));
    }
}