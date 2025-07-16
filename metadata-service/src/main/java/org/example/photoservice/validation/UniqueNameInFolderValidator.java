package org.example.photoservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.photoservice.repository.FolderItemRepository;
import org.springframework.stereotype.Component;

@Component
public class UniqueNameInFolderValidator implements ConstraintValidator<UniqueNameInFolder, Movable> {
    private final FolderItemRepository repository;

    public UniqueNameInFolderValidator(FolderItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValid(Movable movable, ConstraintValidatorContext constraintValidatorContext) {
        return !repository.existsByParentFolderObjectUUIDAndName(movable.getNewParentFolderId(), movable.getFolderItemName());
    }
}
