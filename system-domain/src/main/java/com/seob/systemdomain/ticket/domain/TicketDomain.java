package com.seob.systemdomain.ticket.domain;

import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.ticket.exception.AlreadyUsedTicketException;
import com.seob.systemdomain.ticket.exception.ExpiredTicketException;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketDomain {

    private TicketId id;

    private UserId userId;
    
    private Long eventId;

    private LocalDateTime createdAt;
    
    private LocalDateTime usedAt;
    
    private LocalDateTime expiryDate;

    private Boolean isUsed;
    
    private Boolean isExpired;

    public static TicketDomain create(UserId userId, Long eventId){
        TicketDomain ticketDomain = new TicketDomain();
        ticketDomain.id = TicketId.create(userId);
        ticketDomain.userId = userId;
        ticketDomain.eventId = eventId;
        ticketDomain.createdAt = LocalDateTime.now();
        ticketDomain.expiryDate = LocalDateTime.now().plusDays(30); // 기본 30일 유효기간
        ticketDomain.isUsed = false;
        ticketDomain.isExpired = false;
        return ticketDomain;
    }



    public static TicketDomain of(String id, UserId userId, Long eventId, LocalDateTime createdAt, 
                                  LocalDateTime usedAt, LocalDateTime expiryDate, 
                                  Boolean isUsed, Boolean isExpired) {
        TicketDomain ticketDomain = new TicketDomain();
        ticketDomain.id = TicketId.of(id);
        ticketDomain.userId = userId;
        ticketDomain.eventId = eventId;
        ticketDomain.createdAt = createdAt;
        ticketDomain.usedAt = usedAt;
        ticketDomain.expiryDate = expiryDate;
        ticketDomain.isUsed = isUsed;
        ticketDomain.isExpired = isExpired;
        return ticketDomain;
    }

    public void validateCanUse(){
        if(Boolean.TRUE.equals(isUsed)){
            throw AlreadyUsedTicketException.EXCEPTION;
        }
        if(Boolean.TRUE.equals(isExpired) || 
           (expiryDate != null && LocalDateTime.now().isAfter(expiryDate))) {
            throw ExpiredTicketException.EXCEPTION;
        }
    }

    public void use(){
        validateCanUse();
        isUsed = true;
        usedAt = LocalDateTime.now();
    }
    
    public void expire() {
        this.isExpired = true;
    }


    public boolean isUsed(){
        return Boolean.TRUE.equals(isUsed);
    }

}
