package com.seob.systeminfra.ticket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", indexes = {
    @Index(name = "idx_ticket_userid", columnList = "userId"),
    @Index(name = "idx_ticket_eventid", columnList = "eventId")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TicketEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column
    private Long eventId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isUsed;
    
    @Column
    private LocalDateTime usedAt;
    
    @Column
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    private boolean isExpired;

    public TicketEntity(String id, String userId, Long eventId, LocalDateTime createdAt, boolean isUsed, 
                       LocalDateTime usedAt, LocalDateTime expiryDate, boolean isExpired) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.isUsed = isUsed;
        this.usedAt = usedAt;
        this.expiryDate = expiryDate;
        this.isExpired = isExpired;
    }


    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
        if (isUsed) {
            this.usedAt = LocalDateTime.now();
        } else {
            this.usedAt = null;
        }
    }
    
    public void expire() {
        this.isExpired = true;
    }
    
    public boolean isValid() {
        return !isUsed && !isExpired && 
               (expiryDate == null || LocalDateTime.now().isBefore(expiryDate));
    }
}
