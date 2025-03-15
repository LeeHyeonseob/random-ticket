package com.seob.application.file.controller.dto;

public record FileUploadResponse(
        String fileUrl
) {
    public static FileUploadResponse of(String fileUrl) {
        return new FileUploadResponse(fileUrl);
    }
}
