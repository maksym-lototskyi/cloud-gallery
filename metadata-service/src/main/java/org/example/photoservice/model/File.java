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
public class File extends FolderItem {

    @Column(name = "file_type", nullable = false)
    private String fileType;

    public File() {

    }
    @Transient
    public String getS3Key() {
        return getParentFolder().getS3Key() + getName();
    }

    @Transient
    public String getPathFromRoot(){
        return getParentFolder().getFullPath() + getName();
    }
}
