package org.example.photoservice.exception;

import lombok.Getter;

@Getter
public class PhotoUploadException extends RuntimeException {
    private final int statusCode;
    public PhotoUploadException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }


}
