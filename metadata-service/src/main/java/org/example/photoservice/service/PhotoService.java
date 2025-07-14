package org.example.photoservice.service;

import org.example.photoservice.S3Properties;
import org.example.photoservice.dto.PhotoResponseDto;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.exception.PhotoUploadException;
import org.example.photoservice.mapper.PhotoMapper;
import org.example.photoservice.model.Folder;
import org.example.photoservice.model.Photo;
import org.example.photoservice.repository.FolderRepository;
import org.example.photoservice.repository.PhotoRepository;
import org.example.photoservice.model.UploadStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final FolderRepository folderRepository;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final RabbitTemplate rabbitTemplate;
    private final S3Properties s3Properties;

    public PhotoService(PhotoRepository photoRepository, FolderRepository folderRepository, S3Client s3Client, S3Presigner s3Presigner, RabbitTemplate rabbitTemplate, S3Properties s3Properties) {
        this.photoRepository = photoRepository;
        this.folderRepository = folderRepository;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.rabbitTemplate = rabbitTemplate;
        this.s3Properties = s3Properties;
    }

    @Transactional
    public void uploadPhoto(MultipartFile file, UUID folderId, UUID userId) {
        Folder folder = folderRepository.findByFolderUUID(folderId)
                .orElseThrow(() -> new PhotoUploadException("Folder not found with id: " + folderId, 404));

        if(folder.getUserUUID() == null || !folder.getUserUUID().equals(userId)) {
            throw new PhotoUploadException("You do not have permission to upload photos to this folder.", 403);
        }

        Photo photo = PhotoMapper.mapToPhoto(file, folder, s3Properties.getBucketName());
        photoRepository.save(photo);

        try {
            rabbitTemplate.convertAndSend("s3-exchange", "s3-upload-key", PhotoMapper.mapToEvent(photo, file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PhotoResponseDto getPhoto(UUID folderId, String fileName){
        Photo photo = photoRepository.findByParentFolderFolderUUIDAndName(folderId, fileName)
                .orElseThrow(() -> new NotFoundException("No files with name " + fileName + " found in folder with id: " + folderId));

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(photo.getS3Bucket())
                .key(photo.getS3Key())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(10))
                .build();
        URL s3Url = s3Presigner.presignGetObject(presignRequest)
                .url();

        return PhotoMapper.mapToPhotoResponse(photo, s3Url);
    }

    public List<PhotoResponseDto> getPhotoPage(int page, int pageSize, UUID folderUUID) {

        return photoRepository.findAll(Pageable.ofSize(pageSize).withPage(page))
                .getContent()
                .stream()
                .filter(photo -> photo.getUploadStatus() == UploadStatus.UPLOADED)
                .map(photo -> getPhoto(folderUUID, photo.getName()))
                .toList();
    }

    public void deletePhotoById(Long id){
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Photo not found with id: " + id));

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(photo.getS3Bucket())
                .key(photo.getS3Key())
                .build());

        photoRepository.delete(photo);
    }


}
