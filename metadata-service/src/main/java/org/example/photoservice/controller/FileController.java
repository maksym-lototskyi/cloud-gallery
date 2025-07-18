package org.example.photoservice.controller;

import org.example.photoservice.aspects.AccessPermission;
import org.example.photoservice.dto.FileResponseDto;
import org.example.photoservice.dto.FilePreviewResponseDto;
import org.example.photoservice.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.example.photoservice.helpers.UserIdExtractor.extractUserIdFromAuthentication;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/upload/folder/{folderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AccessPermission(idParam = "folderId")
    public ResponseEntity<String> uploadPhotos(
            @PathVariable UUID folderId,
            @RequestParam("files") MultipartFile[] files
    ) {
        for (MultipartFile file : files) {
            fileService.uploadPhoto(file, folderId);
        }

        return ResponseEntity.ok("Photos uploaded");
    }

    @GetMapping(path = "{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @AccessPermission(idParam = "fileId")
    public ResponseEntity<FileResponseDto> getPhoto(@PathVariable UUID fileId) {
        var res = fileService.getFileDetails(fileId);
        System.out.println(res);
        return ResponseEntity.ok(res);
    }

    @GetMapping()
    public ResponseEntity<List<FilePreviewResponseDto>> getAllFiles(Authentication authentication,
                                                                    @RequestParam(defaultValue = "0", required = false) int page,
                                                                    @RequestParam(defaultValue = "20", required = false) int size) {
        UUID userId = extractUserIdFromAuthentication(authentication);
        List<FilePreviewResponseDto> files = fileService.getFilePage(userId, page, size);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping(path = "{fileId}")
    @AccessPermission(idParam = "fileId")
    public ResponseEntity<String> deletePhoto(@PathVariable UUID fileId) {
        fileService.deleteFileById(fileId);
        return ResponseEntity.ok("File deleted successfully");
    }
}
