package org.example.photoservice.controller;

import jakarta.validation.Valid;
import org.example.photoservice.aspects.AccessPermission;
import org.example.photoservice.dto.response.FolderContentResponseDto;
import org.example.photoservice.dto.request.FolderRequestDto;
import org.example.photoservice.dto.response.FolderResponseDto;
import org.example.photoservice.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static org.example.photoservice.helpers.UserIdExtractor.extractUserIdFromAuthentication;

@RestController
@RequestMapping("/api/folders")
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public ResponseEntity<FolderResponseDto> createFolder(@RequestBody @Valid FolderRequestDto requestDto, Authentication authentication){
        FolderResponseDto createdFolder = folderService.createFolder(requestDto, extractUserIdFromAuthentication(authentication));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{folderId}/content")
                .buildAndExpand(createdFolder.getFileItemId())
                .toUri();

        return ResponseEntity.created(uri).body(createdFolder);
    }

    @GetMapping("/root")
    public ResponseEntity<FolderResponseDto> getRootFolder(Authentication authentication) {
        UUID userId = extractUserIdFromAuthentication(authentication);

        FolderResponseDto rootFolder = folderService.getRootFolder(userId);
        return ResponseEntity.ok(rootFolder);
    }

    @GetMapping("/{folderId}/content")
    @AccessPermission(idParam = "folderId")
    public ResponseEntity<FolderContentResponseDto> getFolderContent(
            @PathVariable UUID folderId) {
        return ResponseEntity.ok(folderService.getFolderContent(folderId));
    }

    @DeleteMapping("/{folderId}")
    @AccessPermission(idParam = "folderId")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable UUID folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.noContent().build();
    }

}
