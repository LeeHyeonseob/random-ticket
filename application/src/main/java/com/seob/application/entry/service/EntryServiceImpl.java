package com.seob.application.entry.service;

import com.seob.application.entry.exception.AlreadyUsedTicketException;
import com.seob.application.entry.exception.EventNotOpendExcpetion;
import com.seob.application.ticket.exception.TicketNotFoundException;
import com.seob.application.user.exception.UserNotFoundException;
import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

        UserDomain user = getUser(userId);

        //이벤트 참가 가능 여부 체크
        if (!eventDomain.canApply()) {
            throw EventNotOpendExcpetion.EXCEPTION;
        }

        //티켓 사용여부 체크
        if(ticket.isUsed()){
            throw AlreadyUsedTicketException.EXCEPTION;
        }

        //티켓 사용 처리
        ticket.use();
        ticketRepository.save(ticket);

        //이벤트 참가
        EntryDomain entryDomain = EntryDomain.create(UserId.of(userId), eventId, ticketId);

        return entryRepository.save(entryDomain);



    }



    @Override
    @Transactional(readOnly = true)
    public List<EntryDomain> findByEventId(Long eventId) {
        return entryRepository.findByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntryDomain> findByUserId(String userId) {

        UserDomain user = getUser(userId);

        return entryRepository.findByUserId(userId);
    }

    @Override
    public List<String> findUserIdByEventId(Long eventId) {
        return entryRepository.findUserIdByEventId(eventId);
    }


    private UserDomain getUser(String userId) {
        return userRepository.findById(UserId.of(userId)).orElseThrow(() -> UserNotFoundException.EXCEPTION);
    }

    private TicketDomain getTicket(String ticketId) {
        return ticketRepository.findById(TicketId.of(ticketId)).orElseThrow(() -> TicketNotFoundException.EXCEPTION);
    }
}
