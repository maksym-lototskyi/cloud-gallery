package org.example.photoservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.photoservice.validation.Movable;
import org.example.photoservice.validation.UniqueNameInFolder;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Getter
@Setter
@UniqueNameInFolder
@Validated
public class FolderRequestDto implements Movable {
    @NotBlank(message = "Folder name cannot be blank")
    @NotNull(message = "Folder name cannot be null")
    private String name;
    @NotNull(message = "Parent folder ID cannot be null")
    private UUID parentFolderId;

    @Override
    public UUID getNewParentFolderId() {
        return parentFolderId;
    }

    @Override
    public String getFolderItemName() {
        return name;
    }

}
