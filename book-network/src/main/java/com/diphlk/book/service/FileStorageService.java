package com.diphlk.book.service;

import com.diphlk.book.model.Book;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${application.file.upload.photos-output-dir}")
    private String fileUploadDir;

    public String storeFile(
                            @NotNull MultipartFile coverImage,
                            @NotNull Integer userId) {
        final String fileUploadSubPath = "user" + File.separator + userId;
        return uploadFile(coverImage, fileUploadSubPath);
    }

    private String uploadFile(@NotNull MultipartFile coverImage, @NotNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadDir + File.separator + fileUploadSubPath;
        File targetFile = new File(finalUploadPath);
        if (!targetFile.exists()) {
            boolean folderCreated = targetFile.mkdirs();
            if (!folderCreated) {
                log.error("Failed to create directory: {}", finalUploadPath);
                throw new RuntimeException("Failed to create directory for file upload.");
            }
        }
        final String fileExtension = getFileExtension(coverImage.getOriginalFilename());
        final String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Path.of(targetFilePath);
        try {
            Files.write(targetPath, coverImage.getBytes());
            log.info("File stored successfully at: {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    private String getFileExtension(String originalFilename) {
        if(originalFilename == null || originalFilename.isEmpty()) {
            return "";
        }
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }
}
