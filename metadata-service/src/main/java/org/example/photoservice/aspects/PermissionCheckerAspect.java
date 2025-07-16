package org.example.photoservice.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.photoservice.helpers.AuthenticationFacade;
import org.example.photoservice.helpers.UserAccessPermissionChecker;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class PermissionCheckerAspect {

    private final UserAccessPermissionChecker userAccessPermissionChecker;

    public PermissionCheckerAspect(UserAccessPermissionChecker userAccessPermissionChecker) {
        this.userAccessPermissionChecker = userAccessPermissionChecker;
    }

    @Around("@annotation(accessPermission)")
    public Object checkAccess(ProceedingJoinPoint joinPoint, AccessPermission accessPermission) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String folderIdParamName = accessPermission.idParam();

        String[] paramNames = methodSignature.getParameterNames();
        UUID folderId = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(folderIdParamName)) {
                folderId = (UUID) args[i];
                break;
            }
        }

        if (folderId == null) {
            throw new IllegalArgumentException("ID parameter not found");
        }

        UUID userId = AuthenticationFacade.getUserId();

        if (!userAccessPermissionChecker.hasAccessToFolder(userId, folderId)) {
            System.out.println("User " + userId + " does not have access to folder " + folderId);
            throw new AccessDeniedException("No access to this item");
        }

        return joinPoint.proceed();
    }
}

