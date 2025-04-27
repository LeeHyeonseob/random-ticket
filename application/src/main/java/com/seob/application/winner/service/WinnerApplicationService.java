package com.seob.application.winner.service;


import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerPublicInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;

import java.util.List;

public interface WinnerApplicationService {

    WinnerDomain selectWinner(Long eventId);

    boolean sendReward(Long winnerId);

    //조회
    List<WinnerUserDetailInfo> getWinnersByUserId(String userId);
    
    //현재 로그인한 사용자의 당첨 내역 조회
    List<WinnerUserDetailInfo> getMyWinners();

    // WinnerDetailInfo를 WinnerRewardDetailInfo로 변경
    List<WinnerRewardDetailInfo> getWinnersByEventId(Long eventId);

    List<WinnerRewardDetailInfo> getAllWinners();

    List<WinnerRewardDetailInfo> getWinnersByStatus(RewardStatus status);
    
    //공개 당첨자 목록 조회 (마스킹 처리)
    List<WinnerPublicInfo> getPublicWinners(Long eventId);
    
    //이름과 이메일로 당첨자 찾기
    boolean checkWinner(String name, String email);
}
