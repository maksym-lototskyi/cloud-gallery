package org.example.photoservice.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("FOLDER")
public class Folder extends S3Object{
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<S3Object> children;
    @Column(name = "folder_uuid", nullable = false, unique = true)
    private UUID folderUUID;
    @Column(name="user_uuid", nullable = false)
    private UUID userUUID;

    @Transient
    public String getFullPath(){
        String parentPath = getParentFolder() != null ? getParentFolder().getFullPath() : "";
        return parentPath + getName() + "/";
    }
}
