package org.example.photoservice.validation;

import java.util.UUID;

public interface Movable {
    UUID getNewParentFolderId();
    String getFolderItemName();
}
