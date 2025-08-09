package org.example.photoservice.service;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.response.FilePreviewResponseDto;
import org.example.photoservice.dto.response.FileResponseDto;
import org.example.photoservice.events.S3ObjectUploadEvent;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.helpers.FileUtils;
import org.example.photoservice.helpers.FolderDeletionChecker;
import org.example.photoservice.helpers.S3LinkPresigner;
import org.example.photoservice.mapper.FileMapper;
import org.example.photoservice.model.File;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.UploadStatus;
import org.example.photoservice.repository.FileRepository;
import org.example.photoservice.repository.FolderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;
    @Mock
    private FolderRepository folderRepository;
    @Mock
    private S3Client s3Client;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private S3Properties s3Properties;
    @Mock
    private S3LinkPresigner s3LinkPresigner;

    @InjectMocks
    private FileService fileService;

    @Test
    void testUploadPhotoSavesFileAndSendsMessage() throws IOException {
        UUID folderId = UUID.randomUUID();
        MultipartFile multipartFile = mock(MultipartFile.class);
        Folder folder = Folder.builder().objectUUID(folderId).build();
        File fileEntity = File.builder().objectUUID(UUID.randomUUID()).build();
        S3ObjectUploadEvent event = S3ObjectUploadEvent.builder()
                .objectId(fileEntity.getId())
                .bucketName(s3Properties.getBucketName())
                .build();

        when(folderRepository.findByObjectUUID(folderId)).thenReturn(Optional.of(folder));
        when(s3Properties.getBucketName()).thenReturn("bucket-name");

        try (MockedStatic<FileMapper> mapperMock = Mockito.mockStatic(FileMapper.class);
             MockedStatic<FileUtils> utilsMock = Mockito.mockStatic(FileUtils.class);
             MockedStatic<FolderDeletionChecker> folderCheckerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {

            folderCheckerMock.when(() -> FolderDeletionChecker.isAccessible(folder)).thenReturn(true);
            mapperMock.when(() -> FileMapper.mapToFile(eq(multipartFile), eq(folder), eq("bucket-name"))).thenReturn(fileEntity);
            utilsMock.when(() -> FileUtils.generateUniqueFileName(anyString(), any())).thenReturn("unique-name");
            when(multipartFile.getOriginalFilename()).thenReturn("original.jpg");
            when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
            mapperMock.when(() -> FileMapper.mapToEvent(eq(fileEntity), any())).thenReturn(event);

            fileService.uploadPhoto(multipartFile, folderId);

            verify(fileRepository).save(fileEntity);
            verify(rabbitTemplate).convertAndSend("s3-exchange", "s3-upload-key", event);
        }
    }

    @Test
    void testUploadPhotoThrowsIfFolderNotFound() {
        UUID folderId = UUID.randomUUID();
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(folderRepository.findByObjectUUID(folderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> fileService.uploadPhoto(multipartFile, folderId));
        verify(fileRepository, never()).save(any());
    }

    @Test
    void testGetFileDetailsReturnsMappedDto() {
        UUID fileId = UUID.randomUUID();
        File fileEntity = File.builder()
                .objectUUID(fileId)
                .s3Bucket("bucket")
                .build();
        FileResponseDto dto = FileResponseDto.builder().fileItemId(fileId).build();

        try (MockedStatic<FileMapper> mapperMock = Mockito.mockStatic(FileMapper.class);
             MockedStatic<FolderDeletionChecker> folderCheckerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {

            when(fileRepository.findByObjectUUID(fileId)).thenReturn(Optional.of(fileEntity));
            folderCheckerMock.when(() -> FolderDeletionChecker.isAccessible(fileEntity)).thenReturn(false);
            when(s3LinkPresigner.generateGetPresignURI("bucket", fileId.toString())).thenReturn(URI.create("http://signed-url").toURL());
            mapperMock.when(() -> FileMapper.mapToDetails(eq(fileEntity), any())).thenReturn(dto);

            FileResponseDto result = fileService.getFileDetails(fileId);

            assertNotNull(result);
            assertEquals(fileId, result.getFileItemId());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetFileDetailsThrowsIfFileNotFound() {
        UUID fileId = UUID.randomUUID();
        when(fileRepository.findByObjectUUID(fileId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> fileService.getFileDetails(fileId));
    }

    @Test
    void testGetFilePageReturnsAccessibleFiles() {
        UUID userId = UUID.randomUUID();
        File file1 = File.builder().objectUUID(UUID.randomUUID()).build();
        FilePreviewResponseDto previewDto = FilePreviewResponseDto.builder().fileItemId(file1.getObjectUUID()).build();

        Page<File> page = new PageImpl<>(List.of(file1));
        try (MockedStatic<FileMapper> mapperMock = Mockito.mockStatic(FileMapper.class);
             MockedStatic<FolderDeletionChecker> folderCheckerMock = Mockito.mockStatic(FolderDeletionChecker.class)) {

            when(fileRepository.findAllByUploadStatusAndUserUUID(eq(UploadStatus.UPLOADED), eq(userId), any())).thenReturn(page);
            folderCheckerMock.when(() -> FolderDeletionChecker.isAccessible(file1)).thenReturn(true);
            mapperMock.when(() -> FileMapper.mapToFilePreview(file1)).thenReturn(previewDto);

            List<FilePreviewResponseDto> result = fileService.getFilePage(userId, 0, 10);

            assertEquals(1, result.size());
            assertEquals(file1.getObjectUUID(), result.get(0).getFileItemId());
        }
    }

    @Test
    void testDeleteFileByIdThrowsIfFileNotFound() {
        UUID fileId = UUID.randomUUID();
        when(fileRepository.findByObjectUUID(fileId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> fileService.deleteFileById(fileId));
    }
}
