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
    @Enumerated(EnumType.STRING)
    @Column(name = "photo_status", nullable = false)
    private UploadStatus uploadStatus;

    public File() {

    }
}
