package com.example.controller;

import com.example.dto.FileUploadRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@Valid @RequestBody FileUploadRequest request) {
        // Process the file upload
        return ResponseEntity.ok("File upload request received: " + request.getFilename());
    }
} 