package com.seob.systemdomain.winner.dto;

import com.seob.systemdomain.winner.vo.RewardStatus;

/**
 * 보상 발송을 위한 최소한의 정보만 포함하는 DTO
 * 성능 최적화를 위해 필요한 필드만 선별
 */
public record WinnerNotificationInfo(
        Long winnerId,
        String userEmail,
        String eventName,
        String rewardUrl,
        RewardStatus status
) {
    
    /**
     * 보상 발송 가능 여부 확인
     */
    public boolean canSendReward() {
        return status == RewardStatus.PENDING;
    }
    
    /**
     * 이메일 마스킹 (로그용)
     */
    public String getMaskedEmail() {
        if (userEmail == null || !userEmail.contains("@")) {
            return userEmail;
        }
        String[] parts = userEmail.split("@");
        String localPart = parts[0];
        String maskedLocal = localPart.substring(0, Math.min(2, localPart.length())) + "***";
        return maskedLocal + "@" + parts[1];
    }
}
