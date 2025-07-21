package com.example.dto;

import com.example.validation.ValidFilename;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FileUploadRequest {
    
    @NotBlank(message = "Filename is required")
    @ValidFilename
    @Pattern(regexp = "^[^/]*$", message = "Filename cannot contain forward slashes")
    private String filename;
    
    // Other fields...
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
} 