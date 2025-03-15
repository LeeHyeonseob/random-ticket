package com.seob.application.winner.controller;

import com.seob.application.winner.controller.dto.WinnerAdminResponse;
import com.seob.application.winner.controller.dto.WinnerUserResponse;
import com.seob.application.winner.service.WinnerApplicationService;
import com.seob.application.winner.service.WinnerFacadeService;
import com.seob.systemdomain.winner.dto.WinnerDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/winners")
@RequiredArgsConstructor
public class WinnerController {

    private final WinnerApplicationService winnerApplicationService;


    //사용자별 당첨 내역 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<WinnerUserResponse>> getWinners(@PathVariable String userId){
        List<WinnerUserDetailInfo> winnersByUserId = winnerApplicationService.getWinnersByUserId(userId);
        List<WinnerUserResponse> responses = winnersByUserId.stream()
                .map(WinnerUserResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }

    //이벤트별 당첨자 조회
    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WinnerAdminResponse>> getWinnersByEvent(@PathVariable Long eventId){
        List<WinnerDetailInfo> winners = winnerApplicationService.getWinnersByEventId(eventId);
        List<WinnerAdminResponse> responses = winners.stream()
                .map(WinnerAdminResponse::of)
                .toList();
        return ResponseEntity.ok(responses);

    }

    //모든 담첨자 조회
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WinnerAdminResponse>> getAllWinners(){
        List<WinnerDetailInfo> winners = winnerApplicationService.getAllWinners();
        List<WinnerAdminResponse> responses = winners.stream()
                .map(WinnerAdminResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }


    //상태별 당첨자 조회
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WinnerAdminResponse>> getWinnersByStatus(@PathVariable String status){
        RewardStatus rewardStatus = RewardStatus.valueOf(status.toUpperCase()); // 나중에 서비스 내부로 넣는거 고려
        List<WinnerDetailInfo> winners = winnerApplicationService.getWinnersByStatus(rewardStatus);
        List<WinnerAdminResponse> responses = winners.stream()
                .map(WinnerAdminResponse::of)
                .toList();
        return ResponseEntity.ok(responses);

    }


    //
}

