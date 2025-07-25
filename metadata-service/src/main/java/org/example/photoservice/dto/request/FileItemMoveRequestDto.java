package org.example.photoservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.photoservice.validation.Movable;
import org.example.photoservice.validation.UniqueNameInFolder;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Getter
@Setter
@Builder
@UniqueNameInFolder
@Validated
public class FileItemMoveRequestDto implements Movable {
    @NotNull
    private UUID sourceFolderId;
    @NotNull
    private UUID newParentFolderId;
    @NotBlank
    private String folderItemName;
}
