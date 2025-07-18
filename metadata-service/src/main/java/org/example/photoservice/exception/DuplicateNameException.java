package org.example.photoservice.exception;

public class DuplicateNameException extends RuntimeException {
    public DuplicateNameException(String s) {
        super(s);
    }
}
