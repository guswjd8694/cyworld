package com.hyunjoying.cyworld.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new IOException("업로드 폴더 생성을 실패했습니다.", e);
            }
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFilename;

        Path destinationPath = uploadPath.resolve(storedFileName);

        multipartFile.transferTo(destinationPath);

        return baseUrl + "/images/" + storedFileName;
    }
}
