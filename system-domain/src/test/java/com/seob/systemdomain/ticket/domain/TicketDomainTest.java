package com.seob.systemdomain.ticket.domain;

import com.seob.systemdomain.ticket.exception.AlreadyUsedTicketException;
import com.seob.systemdomain.user.domain.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketDomainTest {


    @Test
    @DisplayName("티켓 발급")
    void createTicket(){
        //given
        String user_id = "1";
        UserId userId = UserId.of(user_id);

        //when
        TicketDomain ticketDomain = TicketDomain.create(userId);

        //then
        assertThat(ticketDomain).isNotNull();
        assertThat(ticketDomain.getUserId().getValue()).isEqualTo("1");


    }

    @Test
    @DisplayName("티켓 사용")
    void useTicket(){
        //given
        String user_id = "1";
        UserId userId = UserId.of(user_id);

        TicketDomain ticketDomain = TicketDomain.create(userId);

        //when
        ticketDomain.use();

        //then
        assertThat(ticketDomain.isUsed()).isTrue();
    }

    @Test
    @DisplayName("티켓이 이미 발급되었을 시 예외 처리")
    void useTicket_throwException(){
        //given
        String user_id = "1";
        UserId userId = UserId.of(user_id);

        TicketDomain ticketDomain = TicketDomain.create(userId);

        ticketDomain.use();

        //when
        assertThrows(AlreadyUsedTicketException.class, () -> ticketDomain.use());
    }



}