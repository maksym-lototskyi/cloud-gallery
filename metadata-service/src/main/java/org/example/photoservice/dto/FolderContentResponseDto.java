package org.example.photoservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class FolderContentResponseDto {
    List<FolderItemResponseDto> folderItems;
    String folderName;
    UUID parentId;
    UUID folderId;
    String folderPath;
}
