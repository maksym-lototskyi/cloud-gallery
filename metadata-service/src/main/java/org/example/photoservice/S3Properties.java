package org.example.photoservice;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "aws.s3")
@Component
@Validated
public class S3Properties {
    @NotBlank
    private String bucketName;
}
