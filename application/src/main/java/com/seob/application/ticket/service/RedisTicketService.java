package com.seob.application.ticket.service;


import com.seob.application.ticket.exception.DuplicateIssueTicketException;
import com.seob.application.ticket.exception.PublishFailureException;
import com.seob.application.ticket.redis.TicketPublisher;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.service.TicketService;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTicketService implements TicketService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TicketPublisher ticketPublisher;

    //중복 발급 체크용 set key
    private static final String TICKET_ISSUED_SET = "issued_tickets";


    @Override
    public TicketDomain issueTicket(UserId userId) {
        String userKey = userId.getValue();

        Long added = redisTemplate.opsForSet().add(TICKET_ISSUED_SET, userKey);

        if (added == 0) {
            // 중복 발급시 예외 처리
            throw DuplicateIssueTicketException.EXCEPTION;
        }

        TicketDomain ticket = TicketDomain.create(userId);

        try {
            ticketPublisher.publish(ticket);
        } catch (Exception e) {
            // 실제 예외 정보 로깅
            System.err.println("Publishing error: " + e.getMessage());
            e.printStackTrace();

            // publish 실패 시 Set에서 제거
            redisTemplate.opsForSet().remove(TICKET_ISSUED_SET, userKey);
            // publish 예외 발생 처리
            throw PublishFailureException.Exception;
        }

        return ticket;
    }
}
