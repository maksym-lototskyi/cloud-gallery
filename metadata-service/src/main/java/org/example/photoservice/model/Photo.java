package org.example.photoservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@DiscriminatorValue(value = "PHOTO")
public class Photo extends S3Object{

    @Column(name = "file_type", nullable = false)
    private String fileType;

    public Photo() {

    }
    public String getS3Key() {
        return getParentFolder().getFullPath() + "/" + getName();
    }
}
