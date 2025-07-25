package org.example.photoservice.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("FOLDER")
public class Folder extends FolderItem {
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FolderItem> children;
    @Transient
    @Override
    public String getFullPath(){
        return super.getFullPath() +  "/";
    }
}
