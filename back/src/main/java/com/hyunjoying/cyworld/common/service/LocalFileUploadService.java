package com.hyunjoying.cyworld.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Profile("local")
@RequiredArgsConstructor
public class LocalFileUploadService implements FileUploadService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFilename;

        Path destinationPath = uploadPath.resolve(storedFileName);

        multipartFile.transferTo(destinationPath);

        return baseUrl + "/images/" + storedFileName;
    }
}
