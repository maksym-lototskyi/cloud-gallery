package org.example.photoservice.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FolderResponseDto.class, name = "folder"),
        @JsonSubTypes.Type(value = FilePreviewResponseDto.class, name = "file")
})
@Getter
@Setter
@SuperBuilder
public abstract class FolderItemResponseDto {
    private String name;
    private UUID parentFolderId;
    private String uploadTime;
    private UUID fileItemId;
}
