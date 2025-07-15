package org.example.photoservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@Setter
public class FileResponseDto extends FolderItemResponseDto{
    private String fileUrl;
    private String fileType;
}
