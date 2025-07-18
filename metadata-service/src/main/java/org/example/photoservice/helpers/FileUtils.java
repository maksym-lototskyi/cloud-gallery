package org.example.photoservice.helpers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

public class FileUtils {

    public static String generateUniqueFileName(String fileName, Predicate<String> exists){
        int counter = 1;
        String candidate = fileName;
        String base = getBase(fileName);
        String extension = getExtension(fileName);

        while(exists.test(candidate)){
            candidate = combine(counter++, base, extension);
        }
        return candidate;
    }

    private static String combine(int counter, String base, String extension){
        return String.format("%s (%d)%s", getBase(base), counter, getExtension(extension));
    }

    private static String getBase(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
    }

    private static String getExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex == -1 ? "" : fileName.substring(dotIndex);
    }

    public static String normalizeFilePath(String filePath){
        Path path = Paths.get(filePath).normalize();
        return path.toString();
    }
}
