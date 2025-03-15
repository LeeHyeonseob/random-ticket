package com.seob.systeminfra.ticket.service;



import com.seob.systeminfra.ticket.exception.*;
import com.seob.systeminfra.ticket.redis.TicketPublisher;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.service.TicketService;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTicketService implements TicketService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TicketPublisher ticketPublisher;
    private final RedissonClient redissonClient;

    @Value("${redis.ticket.issued-set}")
    private String ticketIssuedSet;

    @Value("${redis.ticket.counter-key}")
    private String ticketCounterKey;

    @Value("${redis.ticket.max-tickets}")
    private int maxTickets;

    @Value("${redis.ticket.lock-key}")
    private String ticketLock;

    @Value("${redis.ticket.lock-wait-time}")
    private long lockWaitTime;

    @Value("${redis.ticket.lock-lease-time}")
    private long lockLeaseTime;



    @Override
    public TicketDomain issueTicket(UserId userId) {
        String userKey = userId.getValue();
        RLock lock = redissonClient.getLock(ticketLock);

        try{
            //락 획득 시도
            boolean isLocked = lock.tryLock(lockWaitTime, lockLeaseTime, TimeUnit.SECONDS);

            if(!isLocked){
                throw TicketServiceOverloadedException.EXCEPTION;
            }
            Long added = redisTemplate.opsForSet().add(ticketIssuedSet, userKey);

            if (added == 0) {
                // 중복 발급시 예외 처리
                throw DuplicateIssueTicketException.EXCEPTION;
            }

            // 티켓 수량 확인 및 증가
            Long currentCount = redisTemplate.opsForValue().increment(ticketCounterKey);

            //발급 가능 수량 초과시 롤백
            if( currentCount > maxTickets ) {
                redisTemplate.opsForValue().decrement(ticketCounterKey,1);
                redisTemplate.opsForSet().remove(ticketIssuedSet, userKey);
                throw TicketExhaustedException.EXCEPTION;
            }


            TicketDomain ticket = TicketDomain.create(userId);

            try {
                ticketPublisher.publish(ticket);
            } catch (Exception e) {
                // 실제 예외 정보 로깅
                System.err.println("Publishing error: " + e.getMessage());
                e.printStackTrace();

                // publish 실패 시 Set에서 제거
                redisTemplate.opsForSet().remove(ticketIssuedSet, userKey);
                redisTemplate.opsForValue().decrement(ticketCounterKey);
                // publish 예외 발생 처리
                throw PublishFailureException.Exception;
            }

            return ticket;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw TicketProcessInterruptedException.EXCEPTION;
        }finally{
            //락 가지고 있으면 해제
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }

    }
}
