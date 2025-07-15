package org.example.photoservice.controller;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.dto.FolderRequestDto;
import org.example.photoservice.dto.FolderResponseDto;
import org.example.photoservice.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<FolderResponseDto> createFolder(@RequestBody FolderRequestDto requestDto, Authentication authentication){
        FolderResponseDto createdFolder = folderService.createFolder(requestDto, extractUserIdFromAuthentication(authentication));
        return ResponseEntity.ok(createdFolder);
    }

    @GetMapping("/root")
    public ResponseEntity<FolderResponseDto> getRootFolder(Authentication authentication) {
        UUID userId = extractUserIdFromAuthentication(authentication);
        FolderResponseDto rootFolder = folderService.getRootFolder(userId);
        return ResponseEntity.ok(rootFolder);
    }

    @GetMapping("/{folderId}/content")
    public ResponseEntity<List<FolderItemResponseDto>> getFolderContent(
            @PathVariable UUID folderId, Authentication authentication) {
        UUID userId = extractUserIdFromAuthentication(authentication);
        List<FolderItemResponseDto> folderContent = folderService.getFolderContent(folderId);
        return ResponseEntity.ok(folderContent);
    }

}
