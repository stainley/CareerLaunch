package com.salapp.job.careerlaunch.userservice.services;

import com.salapp.job.careerlaunch.userservice.exception.ProfilePictureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String fileName) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String contentType = file.getContentType();

        assert contentType != null;
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        String fileType = contentType.substring("image/".length());

        log.info("Storing file: {}", fileName);
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectory(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName + "." + fileType);
            log.info("Storing file: {}", filePath);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException exception) {
            throw new ProfilePictureException("Could not store file " + fileName, exception);
        }
    }

}