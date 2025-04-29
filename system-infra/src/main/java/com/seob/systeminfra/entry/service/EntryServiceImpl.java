package com.seob.systeminfra.entry.service;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.ticket.exception.AlreadyUsedTicketException;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systeminfra.entry.exception.EventNotOpendExcpetion;
import com.seob.systeminfra.entry.exception.TicketNotFoundException;
import com.seob.systeminfra.entry.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public EntryDomain apply(String userId, Long eventId, String ticketId) {
        EventDomain eventDomain = eventRepository.findById(eventId);
        TicketDomain ticket = getTicket(ticketId);
        UserDomain user = getUser(UserId.of(userId));

        // 이벤트 참가 가능 여부 검증
        if (!eventDomain.canApply()) {
            throw EventNotOpendExcpetion.EXCEPTION;
        }

        // 티켓 사용 가능 여부 검증
        if(ticket.isUsed()){
            throw AlreadyUsedTicketException.EXCEPTION;
        }

        // 티켓 사용 처리
        ticket.use();
        ticketRepository.save(ticket);

        // 이벤트 참여 생성 및 저장
        EntryDomain entryDomain = EntryDomain.create(UserId.of(userId), eventId, ticketId);
        return entryRepository.save(entryDomain);
    }

    // 내부 유틸리티 메서드
    private UserDomain getUser(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
    }

    private TicketDomain getTicket(String ticketId) {
        return ticketRepository.findById(TicketId.of(ticketId))
                .orElseThrow(() -> TicketNotFoundException.EXCEPTION);
    }
}
