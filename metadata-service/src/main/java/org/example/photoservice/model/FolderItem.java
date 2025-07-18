package org.example.photoservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "object_type")
public abstract class FolderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String name;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder parentFolder;
    @Column(name = "s3_bucket", nullable = false)
    private String s3Bucket;
    @Column(name = "object_uuid", nullable = false, unique = true)
    private UUID objectUUID;
    @Column(name="user_uuid", nullable = false)
    private UUID userUUID;

    @Transient
    public String getFullPath(){
        String parentPath = getParentFolder() != null ? getParentFolder().getFullPath() : "";
        return parentPath + getName();
    }

}
