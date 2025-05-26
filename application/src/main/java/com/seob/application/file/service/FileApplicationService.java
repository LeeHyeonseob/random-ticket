package com.seob.application.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileApplicationService {
    String uploadFile(MultipartFile file);
    void deleteFile(String fileUrl);
}
