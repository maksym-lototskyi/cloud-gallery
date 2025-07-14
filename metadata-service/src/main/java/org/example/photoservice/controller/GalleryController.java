package org.example.photoservice.controller;

import org.example.photoservice.security_customizers.CustomPrincipal;
import org.example.photoservice.dto.PhotoResponseDto;
import org.example.photoservice.service.PhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class GalleryController {
    private final PhotoService photoService;

    public GalleryController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(path = "/upload/folder/{folderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhotos(
            Authentication auth,
            @PathVariable UUID folderId,
            @RequestParam("files") MultipartFile[] files
    ) {
        UUID userId = extractUserIdFromAuthentication(auth);

        for (MultipartFile file : files) {
            photoService.uploadPhoto(file, folderId, userId);
        }

        return ResponseEntity.ok("Photos uploaded");
    }

    @GetMapping(path = "photo/{fileName}/folder/{folderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhotoResponseDto> getPhoto(@PathVariable String fileName,
                                                     @PathVariable UUID folderId) {

        var res = photoService.getPhoto(folderId, fileName);
        System.out.println(res);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(path = "photo/{id}")
    public ResponseEntity<String> deletePhoto(@PathVariable Long id) {
        photoService.deletePhotoById(id);
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
