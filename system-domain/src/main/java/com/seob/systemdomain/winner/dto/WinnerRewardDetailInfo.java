package com.seob.systemdomain.winner.dto;

import com.seob.systemdomain.winner.vo.RewardStatus;

import java.time.LocalDateTime;

public record WinnerRewardDetailInfo(
        Long winnerId,
        String userId,
        String nickName,
        String userEmail,
        Long eventId,
        String eventName,
        String eventDescription, // 보상 이름 대신 이벤트 설명 사용
        Long rewardId,
        RewardStatus status,
        LocalDateTime sentAt
) {
    public static WinnerRewardDetailInfo of(
            Long winnerId,
            String userId,
            String nickName,
            String userEmail,
            Long eventId,
            String eventName,
            String eventDescription,
            Long rewardId,
            RewardStatus status,
            LocalDateTime sentAt
    ) {
        return new WinnerRewardDetailInfo(
                winnerId, userId, nickName, userEmail, eventId, 
                eventName, eventDescription, rewardId, status, sentAt
        );
    }
}
