package com.seob.application.entry.controller;

import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.application.entry.service.EntryApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/entries")
@RequiredArgsConstructor
@Tag(name = "이벤트 참여", description = "이벤트 참여 내역 관리 API")
public class EntryController {

    private final EntryApplicationService entryApplicationService;

    //현재 로그인한 사용자의 이벤트 참여 내역 조회
    @Operation(
        summary = "내 이벤트 참여 내역 조회",
        description = "현재 로그인한 사용자의 모든 이벤트 참여 내역을 조회합니다. 인증이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "참여 내역 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
        }
    )
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EntryResponse>> getMyEntries() {
        List<EntryResponse> entries = entryApplicationService.getMyEntries();
        return ResponseEntity.ok(entries);
    }

    //특정 사용자의 이벤트 참여 내역 조회 (관리자 전용)
    @Operation(
        summary = "사용자 이벤트 참여 내역 조회",
        description = "특정 사용자의 모든 이벤트 참여 내역을 조회합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "참여 내역 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        }
    )
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EntryResponse>> getUserEntries(@PathVariable String userId) {
        List<EntryResponse> entries = entryApplicationService.getUserEntries(userId);
        return ResponseEntity.ok(entries);
    }

    //특정 이벤트의 참여 내역 조회 (관리자 전용)
    @Operation(
        summary = "이벤트 참여 내역 조회",
        description = "특정 이벤트의 모든 참여 내역을 조회합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "참여 내역 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
        }
    )
    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EntryResponse>> getEventEntries(@PathVariable Long eventId) {
        List<EntryResponse> entries = entryApplicationService.getEventEntries(eventId);
        return ResponseEntity.ok(entries);
    }
}
