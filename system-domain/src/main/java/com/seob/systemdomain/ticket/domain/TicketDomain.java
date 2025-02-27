package com.seob.systemdomain.ticket.domain;

import com.seob.systemdomain.ticket.exception.AlreadyUsedTicketException;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TicketDomain {

    private Long id;

    private UserId userId;

    private LocalDateTime createdAt;

    private Boolean isUsed;


    public static TicketDomain create(UserId userId){
        TicketDomain ticketDomain = new TicketDomain();
        ticketDomain.userId = userId;
        ticketDomain.createdAt = LocalDateTime.now();
        ticketDomain.isUsed = false;
        return ticketDomain;
    }

    public void use(){
        if(Boolean.TRUE.equals(isUsed)){
            throw AlreadyUsedTicketException.EXCEPTION;
        }
        isUsed = true;
    }

    public boolean isUsed(){
        return isUsed;
    }

}
