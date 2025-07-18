package org.example.photoservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FolderItemRenameRequest {
    private String newName;
    private UUID objectId;
}
