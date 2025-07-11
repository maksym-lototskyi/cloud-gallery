package org.example.fileuploadservice.s3;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.auth.credentials.AwsCredentials;

@Component
@Validated
@Setter
@ConfigurationProperties(prefix = "amazon")
public class S3Properties implements AwsCredentials {
    private S3Credentials s3Credentials;

    @Override
    public String accessKeyId() {
        return s3Credentials.getAccessKey();
    }

    @Override
    public String secretAccessKey() {
        return s3Credentials.getSecretKey();
    }

    @Data
    public static class S3Credentials {
        @NotBlank
        private String secretKey;
        @NotBlank
        private String accessKey;
    }
}
