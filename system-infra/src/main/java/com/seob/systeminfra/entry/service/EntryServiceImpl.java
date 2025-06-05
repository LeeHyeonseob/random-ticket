package com.seob.systeminfra.entry.service;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systeminfra.exception.EventNotFoundException;
import com.seob.systeminfra.exception.TicketNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    
    @Override
    public EntryDomain apply(String userId, Long eventId) {
        UserId userIdVo = UserId.of(userId);

        //이벤트 데이터 조회
        EventDomain eventDomain = eventRepository.findById(eventId)
                .orElseThrow(() -> EventNotFoundException.EXCEPTION);

        // 티켓 데이터 조회
        TicketDomain ticket = ticketRepository.findByUserIdAndEventIdAndNotUsed(userIdVo, eventId)
                .orElseThrow(() -> TicketNotFoundException.EXCEPTION);

        ticketRepository.save(ticket);
        EntryDomain entryDomain = EntryDomain.create(userIdVo, eventId, ticket.getId().getValue());
        return entryRepository.save(entryDomain);
    }
}
