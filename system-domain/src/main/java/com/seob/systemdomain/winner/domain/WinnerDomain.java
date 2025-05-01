package com.seob.systemdomain.winner.domain;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.vo.RewardStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WinnerDomain {

    private Long id;

    private UserId userId;

    private Long eventId;

    private Long rewardId;
    
    private Long entryId;

    private RewardStatus status;

    private LocalDateTime sentAt;

    public static WinnerDomain create(UserId userId, Long eventId, Long rewardId, Long entryId) {
        WinnerDomain winnerDomain = new WinnerDomain();
        winnerDomain.userId = userId;
        winnerDomain.eventId = eventId;
        winnerDomain.rewardId = rewardId;
        winnerDomain.entryId = entryId;
        winnerDomain.status = RewardStatus.PENDING;

        return winnerDomain;
    }
    

    public static WinnerDomain create(UserId userId, Long eventId, Long rewardId) {
        return create(userId, eventId, rewardId, null);
    }

    public static WinnerDomain of(Long id, String userId, Long eventId, Long rewardId, Long entryId, 
                                 RewardStatus status, LocalDateTime sentAt) {
        WinnerDomain domain = new WinnerDomain();
        domain.id = id;
        domain.userId = UserId.of(userId);
        domain.eventId = eventId;
        domain.rewardId = rewardId;
        domain.entryId = entryId;
        domain.status = status;
        domain.sentAt = sentAt;
        return domain;
    }
    

    public static WinnerDomain of(Long id, String userId, Long eventId, Long rewardId, 
                                 RewardStatus status, LocalDateTime sentAt) {
        return of(id, userId, eventId, rewardId, null, status, sentAt);
    }

    public void send(){
        this.status = RewardStatus.COMPLETE;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = RewardStatus.FAILED;
    }
}
