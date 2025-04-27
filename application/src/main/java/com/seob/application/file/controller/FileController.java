package com.seob.application.file.controller;

import com.seob.application.file.controller.dto.FileUploadResponse;
import com.seob.systeminfra.file.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "파일", description = "파일 업로드 API")
public class FileController {

    private final FileUploadService fileUploadService;

    @Operation(
        summary = "파일 업로드",
        description = "파일을 업로드하고 URL을 반환합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공", 
                        content = @Content(schema = @Schema(implementation = FileUploadResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
        }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileUploadService.uploadFile(file);
        return ResponseEntity.ok(FileUploadResponse.of(fileUrl));
    }

}
