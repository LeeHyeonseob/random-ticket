package com.seob.application.winner.controller;

import com.seob.application.auth.CustomUserDetails;
import com.seob.application.winner.controller.dto.WinnerAdminResponse;
import com.seob.application.winner.controller.dto.WinnerCheckRequest;
import com.seob.application.winner.controller.dto.WinnerPublicResponse;
import com.seob.application.winner.controller.dto.WinnerUserResponse;
import com.seob.application.winner.service.WinnerApplicationService;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerPublicInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/winners")
@RequiredArgsConstructor
@Tag(name = "당첨자", description = "당첨자 관리 API")
public class WinnerController {

    private final WinnerApplicationService winnerApplicationService;

    //사용자별 당첨 내역 조회 (관리자 전용)
    @Operation(
        summary = "사용자별 당첨 내역 조회",
        description = "특정 사용자의 모든 당첨 내역을 조회합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "당첨 내역 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        }
    )
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WinnerUserResponse>> getWinners(@PathVariable String userId){
        List<WinnerUserDetailInfo> winnersByUserId = winnerApplicationService.getWinnersByUserId(userId);
        List<WinnerUserResponse> responses = winnersByUserId.stream()
                .map(WinnerUserResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }

    //이벤트별 당첨자 조회 (관리자 전용)
    @Operation(
        summary = "이벤트별 당첨자 조회",
        description = "특정 이벤트의 모든 당첨자를 조회합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "당첨자 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
        }
    )
    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WinnerAdminResponse>> getWinnersByEvent(@PathVariable Long eventId){
        List<WinnerRewardDetailInfo> winners = winnerApplicationService.getWinnersByEventId(eventId);
        List<WinnerAdminResponse> responses = winners.stream()
                .map(WinnerAdminResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }

    //모든 당첨자 조회 (관리자 전용)
    @Operation(
        summary = "모든 당첨자 조회",
        description = "모든 이벤트의 당첨자를 조회합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "당첨자 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
        }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WinnerAdminResponse>> getAllWinners(){
        List<WinnerRewardDetailInfo> winners = winnerApplicationService.getAllWinners();
        List<WinnerAdminResponse> responses = winners.stream()
                .map(WinnerAdminResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }

    //상태별 당첨자 조회 (관리자 전용)
    @Operation(
        summary = "상태별 당첨자 조회",
        description = "특정 상태(예: 대기 중, 완료)인 당첨자를 조회합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "당첨자 조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 상태값"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
        }
    )
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WinnerAdminResponse>> getWinnersByStatus(@PathVariable String status){
        RewardStatus rewardStatus = RewardStatus.valueOf(status.toUpperCase()); // 나중에 서비스 내부로 넣는거 고려
        List<WinnerRewardDetailInfo> winners = winnerApplicationService.getWinnersByStatus(rewardStatus);
        List<WinnerAdminResponse> responses = winners.stream()
                .map(WinnerAdminResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }

    //공개 당첨자 목록 조회
    @Operation(
        summary = "공개 당첨자 목록 조회",
        description = "공개 설정된 당첨자 목록을 조회합니다. 선택적으로 이벤트 ID를 지정할 수 있습니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "당첨자 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음 (이벤트 ID가 제공된 경우)")
        }
    )
    @GetMapping("/public")
    public ResponseEntity<List<WinnerPublicResponse>> getPublicWinners(
            @RequestParam(required = false) Long eventId) {
        
        List<WinnerPublicInfo> publicWinners = winnerApplicationService.getPublicWinners(eventId);
        
        List<WinnerPublicResponse> responses = publicWinners.stream()
                .map(WinnerPublicResponse::of)
                .toList();
                
        return ResponseEntity.ok(responses);
    }
    
    //이름과 이메일로 당첨 여부 확인
    @Operation(
        summary = "당첨 여부 확인",
        description = "이름과 이메일을 기준으로 당첨 여부를 확인합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "당첨 여부 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
        }
    )
    @PostMapping("/check")
    public ResponseEntity<Boolean> checkWinner(@RequestBody WinnerCheckRequest request) {
        boolean isWinner = winnerApplicationService.checkWinner(
                request.name(), 
                request.email()
        );
        return ResponseEntity.ok(isWinner);
    }
    
    //현재 로그인한 사용자의 당첨 내역 조회
    @Operation(
        summary = "내 당첨 내역 조회",
        description = "현재 로그인한 사용자의, 모든 당첨 내역을 조회합니다. 인증이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "당첨 내역 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
        }
    )
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WinnerUserResponse>> getMyWinners(@AuthenticationPrincipal CustomUserDetails user) {
        List<WinnerUserDetailInfo> myWinners = winnerApplicationService.getMyWinners(user.getUserId());
        List<WinnerUserResponse> responses = myWinners.stream()
                .map(WinnerUserResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }

    //수동 보상 발송 (관리자 전용)
    @Operation(
        summary = "수동 보상 발송",
        description = "특정 당첨자에게 수동으로 보상을 발송합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "보상 발송 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "당첨자를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 발송된 보상")
        }
    )
    @PostMapping("/{winnerId}/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendReward(@PathVariable Long winnerId) {
        winnerApplicationService.sendRewardManually(winnerId);
        return ResponseEntity.ok().build();
    }
}