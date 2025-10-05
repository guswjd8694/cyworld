package com.hyunjoying.cyworld.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class DebugController {


    // 서버 살아있는지 체크
    @GetMapping("/ping")
    public String ping() {
        System.out.println("### PING API HIT! ###");
        return "pong";
    }


    // 데이터 잘 가는지 체크
    @PostMapping("/debug/echo")
    public ResponseEntity<String> echo(@RequestBody String body) {
        System.out.println("### DEBUG ECHO API HIT! BODY: " + body);
        return ResponseEntity.ok("Received from server: " + body);
    }


    // 버전 체크
    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok("VERSION_2025_09_17_FINAL");
    }


    // 파일 업로드 시 폴더 상태 점검
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/debug/filesystem")
    public ResponseEntity<String> checkFileSystem() {
        StringBuilder result = new StringBuilder();

        result.append("1. 'file.upload-dir' property value: [").append(uploadDir).append("]\n\n");

        Path uploadPath = Paths.get(uploadDir);
        File uploadDirFile = uploadPath.toFile();

        result.append("2. Directory exists: ").append(uploadDirFile.exists()).append("\n");
        result.append("3. Is it a directory: ").append(uploadDirFile.isDirectory()).append("\n");
        result.append("4. Can we read it: ").append(uploadDirFile.canRead()).append("\n");
        result.append("5. Can we write to it: ").append(uploadDirFile.canWrite()).append("\n\n");

        try {
            Path testFilePath = uploadPath.resolve("test-file.txt");
            Files.writeString(testFilePath, "This is a test file.");
            result.append("6. Test file created successfully at: ").append(testFilePath).append("\n");

            Files.delete(testFilePath);
            result.append("7. Test file deleted successfully.\n");

            return ResponseEntity.ok(result.toString());
        } catch (Exception e) {
            result.append("8. FAILED to create or delete test file. Exception: ").append(e.toString());
            return ResponseEntity.status(500).body(result.toString());
        }
    }
}