package org.example.photoservice.validation;

import java.util.UUID;

public interface FileUpload {
    UUID getParentId();
    String[] getFileNames();
}
