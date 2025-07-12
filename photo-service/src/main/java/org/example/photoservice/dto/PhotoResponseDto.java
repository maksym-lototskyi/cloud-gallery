package org.example.photoservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class PhotoResponseDto {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String uploadTime;
}
