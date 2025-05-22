package com.seob.systeminfra.ticket.repository;

import com.seob.systemdomain.ticket.repository.TicketIssuanceRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisTicketIssuanceRepository implements TicketIssuanceRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${app.redis.ticket.issued-set}")
    private String ticketIssuedSet;
    
    @Value("${app.redis.ticket.counter-key}")
    private String ticketCounterKey;
    
    @Value("${app.redis.ticket.max-tickets}")
    private int maxTickets;
    
    @Override
    public boolean hasIssuedTicket(UserId userId) {
        Boolean isMember = redisTemplate.opsForSet().isMember(ticketIssuedSet, userId.getValue());
        return Boolean.TRUE.equals(isMember);
    }
    
    @Override
    public void saveIssuance(UserId userId) {
        redisTemplate.opsForSet().add(ticketIssuedSet, userId.getValue());
    }
    
    @Override
    public void cancelIssuance(UserId userId) {
        redisTemplate.opsForSet().remove(ticketIssuedSet, userId.getValue());
    }
    
    @Override
    public long incrementTicketCount() {
        Long currentCount = redisTemplate.opsForValue().increment(ticketCounterKey);
        return currentCount != null ? currentCount : 0;
    }
    
    @Override
    public void decrementTicketCount() {
        redisTemplate.opsForValue().decrement(ticketCounterKey);
    }
    
    @Override
    public int getMaxTickets() {
        return maxTickets;
    }
}
