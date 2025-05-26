package com.seob.application.file.service;

import com.seob.application.file.validator.FileValidator;
import com.seob.systeminfra.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileApplicationServiceImpl implements FileApplicationService {
    
    private final FileUploadService fileUploadService;

    @Override
    public String uploadFile(MultipartFile file) {
        log.info("파일 업로드 요청: 파일명={}, 크기={}bytes, 타입={}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        // 파일 검증
        FileValidator.validate(file);

        String fileUrl = fileUploadService.uploadFile(file);
        
        log.info("파일 업로드 완료: {}", fileUrl);
        return fileUrl;
    }

    @Override
    public void deleteFile(String fileUrl) {
        log.info("파일 삭제 요청: {}", fileUrl);

        fileUploadService.deleteFile(fileUrl);
        
        log.info("파일 삭제 완료: {}", fileUrl);
    }
}
