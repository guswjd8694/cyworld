package com.hyunjoying.cyworld.common.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileUploadService {
    String saveFile(MultipartFile multipartFile) throws IOException;
}