package org.example.photoservice.helpers;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.net.URL;
import java.time.Duration;

@Component
public class S3LinkPresigner {
    private final S3Presigner s3Presigner;

    public S3LinkPresigner(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    public URL generateGetPresignURI(String bucket, String s3Key){
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(10))
                .build();
        return s3Presigner.presignGetObject(presignRequest)
                .url();
    }
}
