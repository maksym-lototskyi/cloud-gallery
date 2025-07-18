package org.example.photoservice.exception;

public class RootFolderRenameException extends RuntimeException {
    public RootFolderRenameException() {
        super("Can not rename the root folder");
    }
}
