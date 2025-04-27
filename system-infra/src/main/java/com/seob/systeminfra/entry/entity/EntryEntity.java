package com.seob.systeminfra.entry.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntryEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long eventId;

    private String ticketId;

    private LocalDateTime createdAt;

    public EntryEntity(String userId, Long eventId, String ticketId, LocalDateTime createdAt) {
        this.userId = userId;
        this.eventId = eventId;
        this.ticketId = ticketId;
        this.createdAt = createdAt;
    }
}
