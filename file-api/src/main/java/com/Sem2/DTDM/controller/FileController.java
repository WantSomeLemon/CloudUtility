package com.Sem2.DTDM.controller;

import com.Sem2.DTDM.entity.FileTask;
import com.Sem2.DTDM.common.dto.FileRequest;
import com.Sem2.DTDM.service.interFace.FileTaskServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @Autowired
    private FileTaskServiceInterface fileTaskService;
    
    //test
    @GetMapping("/test")
    public String hello() {
        return "API đang chạy!";
    }
    
    @PostMapping("/create")
    public ResponseEntity<FileTask> createTask(@RequestBody FileRequest request) {
        FileTask createTask = fileTaskService.createTask(request.getFileName());
        return ResponseEntity.ok(createTask);
//        return null;
    }
}
