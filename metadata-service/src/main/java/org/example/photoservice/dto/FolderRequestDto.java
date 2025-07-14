package org.example.photoservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FolderRequestDto {
    private String name;
    private UUID parentFolderId;
}
