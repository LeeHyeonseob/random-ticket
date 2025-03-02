package com.seob.insystemavailable.winner.entity;

import com.seob.systemdomain.winner.vo.RewardStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "winners")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WinnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long eventId;

    private Long rewardId;

    @Enumerated(EnumType.STRING)
    private RewardStatus status;

    private LocalDateTime sentAt;

    public WinnerEntity(String userId, Long eventId, Long rewardId, RewardStatus status, LocalDateTime sentAt) {
        this.userId = userId;
        this.eventId = eventId;
        this.rewardId = rewardId;
        this.status = status;
        this.sentAt = sentAt;
    }



}
