package com.seob.application.file.validator;

import com.seob.systemcore.error.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_FORMATS = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw FileUploadException.INVALID_FILE_FORMAT;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw FileUploadException.FILE_SIZE_EXCEEDED;
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedFormat(contentType)) {
            throw FileUploadException.INVALID_FILE_FORMAT;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw FileUploadException.INVALID_FILE_FORMAT;
        }
    }

    private static boolean isAllowedFormat(String contentType) {
        return ALLOWED_FORMATS.contains(contentType.toLowerCase());
    }
}
