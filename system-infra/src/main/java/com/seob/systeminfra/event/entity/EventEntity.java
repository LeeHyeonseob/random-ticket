package com.seob.systeminfra.event.entity;

import com.seob.systemdomain.event.vo.EventStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDate eventDate;

    private LocalDateTime createdAt;

    public EventEntity(Long id, String name, String description, EventStatus status, LocalDate eventDate, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.eventDate = eventDate;
        this.createdAt = createdAt;

    }
}
