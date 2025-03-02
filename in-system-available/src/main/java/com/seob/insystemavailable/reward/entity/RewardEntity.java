package com.seob.insystemavailable.reward.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private String resource_url;

    private LocalDateTime createdAt;


    public RewardEntity(Long eventId, String resource_url, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.resource_url = resource_url;
        this.createdAt = createdAt;
    }


}
