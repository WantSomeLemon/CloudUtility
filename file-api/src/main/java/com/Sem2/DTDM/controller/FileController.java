package com.Sem2.DTDM.controller;

import com.Sem2.DTDM.common.entity.ConversionType;
import com.Sem2.DTDM.common.entity.FileTask;
import com.Sem2.DTDM.common.dto.FileRequest;
import com.Sem2.DTDM.service.interFace.FileTaskServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    
    private final FileTaskServiceInterface fileTaskService;

    @Value("${app.storage.path}")
    private String baseStoragePath;
    
    //test
    @GetMapping("/test")
    public String hello() {
        return "API đang chạy!";
    }

    @PostMapping("/upload")
    public ResponseEntity<FileTask> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") ConversionType type) {

        try {
            log.info("Received file: {} with type: {}", file.getOriginalFilename(), type);

            // 1. Save file to local storage (/app/storage/)
            Path root = Paths.get(baseStoragePath);
            if (!Files.exists(root)) Files.createDirectories(root);

            Path targetPath = root.resolve(file.getOriginalFilename());
            if (Files.exists(targetPath)) {
                Files.delete(targetPath);
            }
            Files.copy(file.getInputStream(), targetPath);

            // 2. Create task in DB and push to Queue
            FileRequest request = new FileRequest();
            request.setFileName(file.getOriginalFilename());

            FileTask task = fileTaskService.createTask(request, type);

            return ResponseEntity.ok(task);

        } catch (Exception e) {
            log.error("Upload failed for file: {}. Error details: ", file.getOriginalFilename(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
