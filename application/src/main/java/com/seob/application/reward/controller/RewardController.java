package com.seob.application.reward.controller;

import com.seob.application.reward.controller.dto.RegisterRewardRequest;
import com.seob.application.reward.controller.dto.RewardResponse;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    // 보상 등록
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
    @GetMapping("/events/{eventId}")
    public ResponseEntity<RewardResponse> getRewardByEventId(@PathVariable Long eventId){
        RewardDomain rewardDomain = rewardService.getRewardByEventId(eventId);
        return ResponseEntity.ok(RewardResponse.of(rewardDomain));
    }






}
