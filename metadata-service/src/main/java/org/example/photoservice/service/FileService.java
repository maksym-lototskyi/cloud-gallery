package org.example.photoservice.service;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.response.FilePreviewResponseDto;
import org.example.photoservice.dto.response.FileResponseDto;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.helpers.FileUtils;
import org.example.photoservice.helpers.FolderDeletionChecker;
import org.example.photoservice.helpers.S3LinkPresigner;
import org.example.photoservice.mapper.FileMapper;
import org.example.photoservice.model.File;
import org.example.photoservice.model.Folder;
import org.example.photoservice.repository.FolderRepository;
import org.example.photoservice.repository.FileRepository;
import org.example.photoservice.model.UploadStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final S3Client s3Client;
    private final RabbitTemplate rabbitTemplate;
    private final S3Properties s3Properties;
    private final S3LinkPresigner s3LinkPresigner;

    public FileService(FileRepository fileRepository, FolderRepository folderRepository, S3Client s3Client, RabbitTemplate rabbitTemplate, S3Properties s3Properties, S3LinkPresigner s3LinkPresigner) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.s3Client = s3Client;
        this.s3LinkPresigner = s3LinkPresigner;
        this.rabbitTemplate = rabbitTemplate;
        this.s3Properties = s3Properties;
    }

    @Transactional
    public void uploadPhoto(MultipartFile file, UUID folderId) {
        Folder folder = folderRepository.findByObjectUUID(folderId)
                .orElseThrow(() -> new NotFoundException("Folder with id " + folderId + " not found"));

        if (!FolderDeletionChecker.isAccessible(folder)) {
            throw new NotFoundException("Folder with id " + folderId + " not found");
        }

        File fileToSave = FileMapper.mapToFile(file, folder, s3Properties.getBucketName());

        adjustFileName(file, folderId, fileToSave);
        fileRepository.save(fileToSave);

        try {
            rabbitTemplate.convertAndSend("s3-exchange", "s3-upload-key", FileMapper.mapToEvent(fileToSave, file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void adjustFileName(MultipartFile file, UUID folderId, File fileToSave) {
        String newName = FileUtils.generateUniqueFileName(
                file.getOriginalFilename(),
                (n) -> fileRepository.existsByParentFolderObjectUUIDAndName(folderId, n)
        );
        fileToSave.setName(newName);
    }

    public FileResponseDto getFileDetails(UUID fileUUID) {
        File file = fileRepository.findByObjectUUID(fileUUID)
                .orElseThrow(() -> new NotFoundException("No file found with id: " + fileUUID));

        if (FolderDeletionChecker.isAccessible(file)) {
            throw new NotFoundException("No file found with id: " + fileUUID);
        }

        return FileMapper.mapToDetails(file, s3LinkPresigner.generateGetPresignURI(file.getS3Bucket(), file.getObjectUUID().toString()));
    }

    public List<FilePreviewResponseDto> getFilePage(UUID userId, int page, int pageSize) {

        return fileRepository.findAllByUploadStatusAndUserUUID(UploadStatus.UPLOADED, userId, Pageable.ofSize(pageSize).withPage(page))
                .getContent()
                .stream()
                .filter(f -> FolderDeletionChecker.isAccessible(f))
                .map(FileMapper::mapToFilePreview)
                .toList();
    }

    public void deleteFileById(UUID objectId) {
        File file = fileRepository.findByObjectUUID(objectId)
                .orElseThrow(() -> new NotFoundException("No file found with id: " + objectId));

        if (FolderDeletionChecker.isAccessible(file)) {
            throw new NotFoundException("No file found with id: " + objectId);
        }

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(file.getS3Bucket())
                .key(file.getObjectUUID().toString())
                .build());

        fileRepository.delete(file);
    }
}
