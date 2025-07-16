package org.example.photoservice.mapper;

import org.example.photoservice.dto.FolderItemResponseDto;
import org.example.photoservice.model.FolderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class S3ObjectMapperStrategyRegistry {
    private final List<S3ObjectMapperStrategy> strategies;

    public S3ObjectMapperStrategyRegistry(List<S3ObjectMapperStrategy> strategies) {
        this.strategies = strategies;
    }

    public FolderItemResponseDto map(FolderItem folderItem){
        for (S3ObjectMapperStrategy strategy : strategies) {
            if (strategy.supports(folderItem)) {
                return strategy.map(folderItem);
            }
        }
        throw new IllegalArgumentException("No suitable mapper found for S3Object type: " + folderItem.getClass().getName());
    }
}
