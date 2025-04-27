package com.seob.systemdomain.winner.dto;

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
        String status,
        LocalDateTime sentAt
) {
    // 팩토리 메서드 추가
    public static WinnerRewardDetailInfo of(
            Long winnerId,
            String userId,
            String nickName,
            String userEmail,
            Long eventId,
            String eventName,
            String eventDescription,
            Long rewardId,
            String status,
            LocalDateTime sentAt
    ) {
        return new WinnerRewardDetailInfo(
                winnerId, userId, nickName, userEmail, eventId, 
                eventName, eventDescription, rewardId, status, sentAt
        );
    }
}
