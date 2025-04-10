package com.seob.systeminfra.ticket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TicketEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isUsed;


    public TicketEntity(String id, String userId, LocalDateTime createdAt, boolean isUsed) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.isUsed = isUsed;
    }

    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }


}
