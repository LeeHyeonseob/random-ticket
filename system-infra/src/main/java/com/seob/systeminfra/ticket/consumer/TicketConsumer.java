package com.seob.systeminfra.ticket.consumer;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TicketConsumer implements StreamListener<String, ObjectRecord<String, TicketDomain>> {

    private final TicketRepository ticketRepository;

    @Override
    public void onMessage(ObjectRecord<String, TicketDomain> message) {

        TicketDomain ticket = message.getValue();

        ticketRepository.save(ticket);
    }
}