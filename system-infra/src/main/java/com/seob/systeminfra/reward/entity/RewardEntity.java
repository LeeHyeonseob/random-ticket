package com.seob.systeminfra.reward.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rewards", indexes = {
    @Index(name = "idx_reward_eventid", columnList = "eventId", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private String name;

    @Column(name = "resource_url")
    private String resourceUrl;

    private LocalDateTime createdAt;


    public RewardEntity(Long eventId, String name, String resourceUrl, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.name = name;
        this.resourceUrl = resourceUrl;
        this.createdAt = createdAt;
    }


}
