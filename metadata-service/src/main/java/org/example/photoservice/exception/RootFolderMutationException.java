package org.example.photoservice.exception;

public class RootFolderMutationException extends RuntimeException {
    public RootFolderMutationException() {
        super("Can not rename or delete the root folder");
    }
}
