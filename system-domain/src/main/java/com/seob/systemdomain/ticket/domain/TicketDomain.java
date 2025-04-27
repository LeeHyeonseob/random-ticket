package com.seob.systemdomain.ticket.domain;

import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.ticket.exception.AlreadyUsedTicketException;
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

    private LocalDateTime createdAt;
    
    private LocalDateTime usedAt;

    private Boolean isUsed;
    
    private Boolean isExpired;


    public static TicketDomain create(UserId userId){
        TicketDomain ticketDomain = new TicketDomain();
        ticketDomain.id = TicketId.create(userId);
        ticketDomain.userId = userId;
        ticketDomain.createdAt = LocalDateTime.now();
        ticketDomain.isUsed = false;
        ticketDomain.isExpired = false;
        return ticketDomain;
    }

    public static TicketDomain of(String id, UserId userId, LocalDateTime createdAt, Boolean isUsed) {
        TicketDomain ticketDomain = new TicketDomain();
        ticketDomain.id = TicketId.of(id);
        ticketDomain.userId = userId;
        ticketDomain.createdAt = createdAt;
        ticketDomain.isUsed = isUsed;
        ticketDomain.isExpired = false;
        return ticketDomain;
    }

    public void use(){
        if(Boolean.TRUE.equals(isUsed)){
            throw AlreadyUsedTicketException.EXCEPTION;
        }
        isUsed = true;
        usedAt = LocalDateTime.now();
    }

    public boolean isUsed(){
        return Boolean.TRUE.equals(isUsed);
    }
}
