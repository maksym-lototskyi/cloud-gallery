package org.example.photoservice.controller;

import org.example.photoservice.dto.FileItemMoveRequestDto;
import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.service.FolderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/folder-items")
public class FolderItemController {

    private final FolderItemService service;

    public FolderItemController(FolderItemService service) {
        this.service = service;
    }

    @PutMapping("/move")
    public ResponseEntity<FolderItemResponseDto> moveFile(@RequestBody FileItemMoveRequestDto request) {
        var result = service.moveObject(request);
        return ResponseEntity.ok(result);
    }
}
