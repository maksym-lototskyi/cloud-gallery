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

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhotos(
            Authentication auth,
            @RequestParam("files") MultipartFile[] files
    ) {
        UUID userId = extractUserIdFromAuthentication(auth);

        for (MultipartFile file : files) {
            photoService.uploadPhoto(file, userId);
        }

        return ResponseEntity.ok("Photos uploaded");
    }

    @GetMapping(path = "photo/{s3Key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhotoResponseDto> getPhoto(@PathVariable String s3Key, Authentication authentication) {

        var res = photoService.getPhoto(s3Key, extractUserIdFromAuthentication(authentication));
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
