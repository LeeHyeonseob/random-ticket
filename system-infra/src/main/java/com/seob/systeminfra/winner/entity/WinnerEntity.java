package com.seob.systeminfra.winner.entity;

import com.seob.systemdomain.winner.vo.RewardStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "winners", indexes = {
    @Index(name = "idx_winner_userid", columnList = "userId"),
    @Index(name = "idx_winner_eventid", columnList = "eventId"),
    @Index(name = "idx_winner_entryid", columnList = "entryId", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WinnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long eventId;

    private Long rewardId;
    
    private Long entryId;

    @Enumerated(EnumType.STRING)
    private RewardStatus status;

    private LocalDateTime sentAt;

    public WinnerEntity(String userId, Long eventId, Long rewardId, Long entryId, 
                       RewardStatus status, LocalDateTime sentAt) {
        this.userId = userId;
        this.eventId = eventId;
        this.rewardId = rewardId;
        this.entryId = entryId;
        this.status = status;
        this.sentAt = sentAt;
    }


    public void updateStatus(RewardStatus status) {
        this.status = status;
    }

    public void markAsSent() {
        this.status = RewardStatus.COMPLETE;
        this.sentAt = LocalDateTime.now();
    }

}
