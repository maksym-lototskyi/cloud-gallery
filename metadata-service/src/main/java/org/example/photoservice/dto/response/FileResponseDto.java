package org.example.photoservice.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class FileResponseDto extends FilePreviewResponseDto{
    private String url;
}
