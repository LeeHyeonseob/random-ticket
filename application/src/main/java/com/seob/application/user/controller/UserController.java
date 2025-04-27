package com.seob.application.user.controller;

import com.seob.application.user.controller.dto.UserProfileRequest;
import com.seob.application.user.controller.dto.UserProfileResponse;
import com.seob.application.user.controller.dto.UserTicketResponse;
import com.seob.application.user.service.UserApplicationService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "사용자", description = "사용자 정보 관리 API")
public class UserController {

    private final UserApplicationService userApplicationService;


    @Operation(
        summary = "내 프로필 조회",
        description = "현재 로그인한 사용자의 프로필 정보를 조회합니다. 인증이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공", 
                        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
        }
    )
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        UserProfileResponse profile = userApplicationService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }


    @Operation(
        summary = "프로필 업데이트",
        description = "현재 로그인한 사용자의 프로필 정보를 업데이트합니다. 인증이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "프로필 업데이트 성공", 
                        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
        }
    )
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@RequestBody UserProfileRequest request) {
        UserProfileResponse updatedProfile = userApplicationService.updateUserProfile(request);
        return ResponseEntity.ok(updatedProfile);
    }


    @Operation(
        summary = "내 티켓 목록 조회",
        description = "현재 로그인한 사용자의 티켓 목록을 조회합니다. 인증이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "티켓 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
        }
    )
    @GetMapping("/me/tickets")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserTicketResponse>> getUserTickets() {
        List<UserTicketResponse> tickets = userApplicationService.getUserTickets();
        return ResponseEntity.ok(tickets);
    }


    @Operation(
        summary = "사용자 프로필 조회",
        description = "특정 사용자의 프로필 정보를 조회합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공", 
                        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        }
    )
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String userId) {
        UserProfileResponse profile = userApplicationService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
}
