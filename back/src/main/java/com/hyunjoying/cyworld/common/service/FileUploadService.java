package com.hyunjoying.cyworld.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String newFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        File destinationFile = new File(Paths.get(uploadDir, newFileName).toString());

        if (!destinationFile.getParentFile().exists() && !destinationFile.getParentFile().mkdirs()) {
            throw new IOException("업로드 폴더 생성을 실패했습니다.");
        }

        multipartFile.transferTo(destinationFile);

        return "/images/" + newFileName;
    }
}
