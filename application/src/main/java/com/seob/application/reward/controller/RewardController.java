package com.seob.application.reward.controller;

import com.seob.application.reward.controller.dto.RegisterRewardRequest;
import com.seob.application.reward.controller.dto.RewardResponse;
import com.seob.application.reward.service.RewardApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
@Tag(name = "보상", description = "이벤트 보상 관리 API")
public class RewardController {

    private final RewardApplicationService rewardApplicationService;

    @Operation(
            summary = "보상 등록",
            description = "특정 이벤트에 보상을 등록합니다. 관리자 권한이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "보상 등록 성공",
                            content = @Content(schema = @Schema(implementation = RewardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RewardResponse> registerReward(@RequestBody RegisterRewardRequest request) {
        RewardResponse response = rewardApplicationService.createReward(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "이벤트 보상 미리보기",
            description = "특정 이벤트의 보상 정보를 미리보기 형태로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "보상 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = RewardResponse.class))),
                    @ApiResponse(responseCode = "404", description = "이벤트 또는 보상을 찾을 수 없음")
            }
    )
    @GetMapping("/events/{eventId}")
    public ResponseEntity<RewardResponse> getRewardByEventId(@PathVariable Long eventId) {
        RewardResponse response = rewardApplicationService.getRewardByEventId(eventId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "모든 보상 목록 조회",
            description = "시스템에 등록된 모든 보상 목록을 조회합니다. 관리자 권한이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "보상 목록 조회 성공"),
                    @ApiResponse(responseCode = "403", description = "권한 없음")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RewardResponse>> getAllRewards() {
        List<RewardResponse> response = rewardApplicationService.getAllRewards();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "보상 수정",
            description = "등록된 보상 정보를 수정합니다. 관리자 권한이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "보상 수정 성공",
                            content = @Content(schema = @Schema(implementation = RewardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "보상을 찾을 수 없음")
            }
    )
    @PutMapping("/{rewardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RewardResponse> updateReward(
            @PathVariable Long rewardId,
            @RequestBody RegisterRewardRequest request) {
        RewardResponse response = rewardApplicationService.updateReward(rewardId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "보상 삭제",
            description = "등록된 보상을 삭제합니다. 관리자 권한이 필요합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "보상 삭제 성공"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "보상을 찾을 수 없음")
            }
    )
    @DeleteMapping("/{rewardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReward(@PathVariable Long rewardId) {
        rewardApplicationService.deleteReward(rewardId);
        return ResponseEntity.noContent().build();
    }
}
