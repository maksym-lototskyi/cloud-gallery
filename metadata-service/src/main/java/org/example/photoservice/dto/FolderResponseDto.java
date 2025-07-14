package org.example.photoservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class FolderResponseDto {
    private String name;
    private UUID folderId;
    private String path;
    private UUID parentFolderId;
}
