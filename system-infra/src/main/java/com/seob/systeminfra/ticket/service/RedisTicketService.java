package com.seob.systeminfra.ticket.service;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.exception.DuplicateTicketIssuanceException;
import com.seob.systemdomain.ticket.exception.TicketExhaustedException;
import com.seob.systemdomain.ticket.exception.TicketProcessException;
import com.seob.systemdomain.ticket.exception.TicketPublishException;
import com.seob.systemdomain.ticket.exception.TicketServiceOverloadedException;
import com.seob.systemdomain.ticket.repository.TicketIssuanceRepository;
import com.seob.systemdomain.ticket.service.TicketService;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systeminfra.ticket.redis.TicketPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTicketService implements TicketService {

    private final TicketPublisher ticketPublisher;
    private final RedissonClient redissonClient;
    private final TicketIssuanceRepository issuanceRepository;

    @Value("${app.redis.ticket.lock-key}")
    private String ticketLock;

    @Value("${app.redis.ticket.lock-wait-time}")
    private long lockWaitTime;

    @Value("${app.redis.ticket.lock-lease-time}")
    private long lockLeaseTime;

    @Override
    public TicketDomain issueTicket(UserId userId, Long eventId) {
        RLock lock = redissonClient.getLock(ticketLock);

        try {
            acquireLock(lock);
            validateTicketIssuance(userId);

            TicketDomain ticket = TicketDomain.create(userId, eventId);

            try {
                ticketPublisher.publish(ticket);
                completeIssuance(userId);
                log.info("사용자 티켓 발급 성공 - 사용자: {}, 이벤트: {}", userId.getValue(), eventId);
                return ticket;
            } catch (Exception e) {
                cancelIssuance(userId);
                log.error("사용자 티켓 이벤트 발행 실패: {}", userId.getValue(), e);
                throw TicketPublishException.EXCEPTION;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("사용자 티켓 발급 중단됨: {}", userId.getValue(), e);
            throw TicketProcessException.EXCEPTION;
        } finally {
            releaseLock(lock);
        }
    }

    // 티켓 발급 가능 여부 검증
    private void validateTicketIssuance(UserId userId) {
        if (issuanceRepository.hasIssuedTicket(userId)) {
            throw DuplicateTicketIssuanceException.EXCEPTION;
        }

        long currentCount = issuanceRepository.incrementTicketCount();
        if (currentCount > issuanceRepository.getMaxTickets()) {
            issuanceRepository.decrementTicketCount();
            throw TicketExhaustedException.EXCEPTION;
        }
    }

    private void completeIssuance(UserId userId) {
        issuanceRepository.saveIssuance(userId);
    }

    private void cancelIssuance(UserId userId) {
        issuanceRepository.decrementTicketCount();
        issuanceRepository.cancelIssuance(userId);
    }

    private void acquireLock(RLock lock) throws InterruptedException {
        boolean isLocked = lock.tryLock(lockWaitTime, lockLeaseTime, TimeUnit.SECONDS);
        if (!isLocked) {
            log.warn("티켓 발급용 락 획득 실패. 시스템이 과부하 상태일 수 있습니다.");
            throw TicketServiceOverloadedException.EXCEPTION;
        }
    }

    private void releaseLock(RLock lock) {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
