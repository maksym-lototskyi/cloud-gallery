package org.example.photoservice.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.photoservice.events.UploadType;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FolderResponseDto.class, name = "folder"),
        @JsonSubTypes.Type(value = FileResponseDto.class, name = "file")
})
@Getter
@Setter
@SuperBuilder
public abstract class FolderItemResponseDto {
    private String name;
    private String path;
    private UUID parentFolderId;
    private String uploadTime;
}
