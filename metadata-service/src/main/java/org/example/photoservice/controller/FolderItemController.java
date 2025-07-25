package org.example.photoservice.controller;

import org.example.photoservice.dto.request.FileItemMoveRequestDto;
import org.example.photoservice.dto.request.FolderItemRenameRequest;
import org.example.photoservice.dto.response.FolderItemResponseDto;
import org.example.photoservice.service.FolderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folder-items")
public class FolderItemController {

    private final FolderItemService service;

    public FolderItemController(FolderItemService service) {
        this.service = service;
    }

    @PutMapping("/move")
    public ResponseEntity<FolderItemResponseDto> moveFolderItem(@RequestBody FileItemMoveRequestDto request) {
        var result = service.moveObject(request);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/rename")
    public ResponseEntity<FolderItemResponseDto> renameFolderItem(@RequestBody FolderItemRenameRequest request) {
        var result = service.rename(request);
        return ResponseEntity.ok(result);
    }
}
