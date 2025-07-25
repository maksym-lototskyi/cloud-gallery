package org.example.photoservice.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@Setter
public class FilePreviewResponseDto extends FolderItemResponseDto{
    private String fileType;
}
