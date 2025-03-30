package com.seob.systeminfra.ticket.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TicketResetScheduler {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.redis.ticket.issued-set}")
    private String ticketIssuedSet;

    @Value("${spring.redis.ticket.counter-key}")
    private String ticketCounterKey;


    @Scheduled(cron = "0 0 0 * * *")
    public void resetTicketSystem() {
        redisTemplate.delete(ticketIssuedSet);
        redisTemplate.opsForValue().set(ticketCounterKey, 0);
        log.info("티켓 정보 초기화");
    }
}
