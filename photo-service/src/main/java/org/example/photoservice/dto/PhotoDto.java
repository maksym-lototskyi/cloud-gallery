package org.example.photoservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class PhotoDto {
    private MultipartFile file;
    private String photoName;
    private String bucketName;
    private String description;
}
