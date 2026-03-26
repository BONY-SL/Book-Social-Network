package com.diphlk.book.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {
    public static byte[] readFileFromPath(String bookCoverUrl) {
        if(StringUtils.isBlank(bookCoverUrl)){
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        try{
            Path filePath = new File(bookCoverUrl).toPath();
            return Files.readAllBytes(filePath);
        } catch (IOException exception){
            log.error("Failed to read file from path: {}", bookCoverUrl, exception);
            throw new RuntimeException("Failed to read file from path: " + bookCoverUrl, exception);
        }
    }
}
