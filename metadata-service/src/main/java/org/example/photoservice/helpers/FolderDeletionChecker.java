package org.example.photoservice.helpers;

import org.example.photoservice.model.Folder;
import org.example.photoservice.model.FolderItem;

public class FolderDeletionChecker {

    public static boolean isAccessible(FolderItem item) {
        Folder parent = item.getParentFolder();
        while (parent != null) {
            if (parent.isDeleted()) return false;
            parent = parent.getParentFolder();
        }
        return !item.isDeleted();
    }

}
