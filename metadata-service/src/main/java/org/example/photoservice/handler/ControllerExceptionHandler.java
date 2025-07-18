package org.example.photoservice.handler;

import org.example.photoservice.exception.DuplicateNameException;
import org.example.photoservice.exception.NotFoundException;
import org.example.photoservice.exception.PhotoUploadException;
import org.example.photoservice.exception.RootFolderRenameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(PhotoUploadException.class)
    public ResponseEntity<String> handlePhotoUploadException(PhotoUploadException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body("File upload failed: " + ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(DuplicateNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDuplicateName(DuplicateNameException e){
        return e.getMessage();
    }

    @ExceptionHandler(RootFolderRenameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handelRootFolderRename(RootFolderRenameException e){
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handle(MethodArgumentNotValidException e){
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                FieldError::getDefaultMessage, Collectors.toList()
                        )
                ));
    }
}
