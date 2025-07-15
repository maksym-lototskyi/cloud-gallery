package org.example.photoservice.mapper;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.model.S3Object;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class S3ObjectMapperStrategyRegistry {
    private final List<S3ObjectMapperStrategy> strategies;

    public S3ObjectMapperStrategyRegistry(List<S3ObjectMapperStrategy> strategies) {
        this.strategies = strategies;
    }

    public FolderItemResponseDto map(S3Object s3Object){
        for (S3ObjectMapperStrategy strategy : strategies) {
            if (strategy.supports(s3Object)) {
                return strategy.map(s3Object);
            }
        }
        throw new IllegalArgumentException("No suitable mapper found for S3Object type: " + s3Object.getClass().getName());
    }
}
