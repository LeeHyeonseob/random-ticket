package com.seob.application.file.controller;

import com.seob.application.file.controller.dto.FileUploadResponse;
import com.seob.application.file.service.FileApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "파일", description = "파일 업로드 관리 API")
public class FileController {

    private final FileApplicationService fileApplicationService;

    @Operation(
        summary = "파일 업로드",
        description = """
            이미지 파일을 S3에 업로드하고 URL을 반환합니다. 관리자 권한이 필요합니다.
            
            **지원 형식**: JPEG, JPG, PNG, GIF, WEBP
            **최대 크기**: 10MB
            **Content-Type**: multipart/form-data
            """,
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공",
                        content = @Content(schema = @Schema(implementation = FileUploadResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 형식 오류, 크기 초과 등)"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "413", description = "파일 크기 초과"),
            @ApiResponse(responseCode = "415", description = "지원하지 않는 파일 형식"),
            @ApiResponse(responseCode = "500", description = "파일 업로드 실패")
        }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(
                description = "업로드할 이미지 파일",
                required = true,
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {
        
        String fileUrl = fileApplicationService.uploadFile(file);
        return ResponseEntity.ok(FileUploadResponse.of(fileUrl));
    }

    @Operation(
        summary = "파일 삭제",
        description = "S3에서 파일을 삭제합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "204", description = "파일 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (잘못된 URL 등)"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
        }
    )
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "삭제할 파일의 S3 URL", required = true)
            @RequestParam("fileUrl") String fileUrl) {
        
        fileApplicationService.deleteFile(fileUrl);
        return ResponseEntity.noContent().build();
    }
}
