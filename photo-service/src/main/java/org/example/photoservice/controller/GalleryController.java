package org.example.photoservice.controller;

import jakarta.validation.Valid;
import org.example.photoservice.security_customizers.CustomPrincipal;
import org.example.photoservice.dto.PhotoDto;
import org.example.photoservice.dto.PhotoResponseDto;
import org.example.photoservice.service.PhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class GalleryController {
    private final PhotoService photoService;

    public GalleryController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(path = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhoto(Authentication authentication, @Valid @ModelAttribute PhotoDto ... dtos) {
        for(var dto : dtos) {
            photoService.uploadPhoto(dto, authentication.getName());
        }
        return ResponseEntity.ok("Photo uploaded successfully");
    }

    @GetMapping(path = "photo/{s3Key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhotoResponseDto> getPhoto(@PathVariable String s3Key, Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        System.out.println(principal.getUserId());
        var res = photoService.getPhoto(s3Key);
        System.out.println(res);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(path = "photo/{id}")
    public ResponseEntity<String> deletePhoto(@PathVariable Long id) {
        photoService.deletePhotoById(id);
        return ResponseEntity.ok("Photo deleted successfully");
    }
}
