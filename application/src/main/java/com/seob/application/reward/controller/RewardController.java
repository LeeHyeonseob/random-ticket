package com.seob.application.reward.controller;

import com.seob.application.reward.controller.dto.RegisterRewardRequest;
import com.seob.application.reward.controller.dto.RewardResponse;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.service.RewardService;
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

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
@Tag(name = "보상", description = "이벤트 보상 관리 API")
public class RewardController {

    private final RewardService rewardService;

    // 보상 등록
    @Operation(
        summary = "보상 등록",
        description = "특정 이벤트에 보상을 등록합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "보상 등록 성공", 
                        content = @Content(schema = @Schema(implementation = RewardResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
        }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RewardResponse> registerReward(@RequestBody RegisterRewardRequest request){
        RewardDomain rewardDomain = rewardService.createReward(
                request.eventId(),
                request.rewardUrl()
        );
        return ResponseEntity.ok(RewardResponse.of(rewardDomain));
    }



    //사용자 이벤트 보상 미리보기 조회
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
    public ResponseEntity<RewardResponse> getRewardByEventId(@PathVariable Long eventId){
        RewardDomain rewardDomain = rewardService.getRewardByEventId(eventId);
        return ResponseEntity.ok(RewardResponse.of(rewardDomain));
    }






}
