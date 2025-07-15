package org.example.photoservice.controller;

import org.example.photoservice.security_customizers.CustomPrincipal;
import org.example.photoservice.dto.FileResponseDto;
import org.example.photoservice.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class GalleryController {
    private final FileService fileService;

    public GalleryController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/upload/folder/{folderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhotos(
            Authentication auth,
            @PathVariable UUID folderId,
            @RequestParam("files") MultipartFile[] files
    ) {
        UUID userId = extractUserIdFromAuthentication(auth);

        for (MultipartFile file : files) {
            fileService.uploadPhoto(file, folderId, userId);
        }

        return ResponseEntity.ok("Photos uploaded");
    }

    @GetMapping(path = "photo/{fileName}/folder/{folderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileResponseDto> getPhoto(@PathVariable String fileName,
                                                    @PathVariable UUID folderId) {

        var res = fileService.getPhoto(folderId, fileName);
        System.out.println(res);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(path = "photo/{id}")
    public ResponseEntity<String> deletePhoto(@PathVariable Long id) {
        fileService.deletePhotoById(id);
        return ResponseEntity.ok("Photo deleted successfully");
    }

    private UUID extractUserIdFromAuthentication(Authentication authentication){
        if (authentication.getPrincipal() instanceof CustomPrincipal customPrincipal) {
            return customPrincipal.getUserId();
        } else {
            throw new IllegalArgumentException("Authentication principal is not of type CustomPrincipal");
        }
    }
}
