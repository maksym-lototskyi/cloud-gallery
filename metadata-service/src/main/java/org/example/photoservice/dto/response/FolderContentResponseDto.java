package org.example.photoservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class FolderContentResponseDto {
    private List<FolderItemResponseDto> folderItems;
    private String folderName;
    private UUID parentId;
    private UUID folderId;
    private String folderPath;
}
