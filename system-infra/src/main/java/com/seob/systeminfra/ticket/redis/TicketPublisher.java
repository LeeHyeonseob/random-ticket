package com.seob.systeminfra.ticket.redis;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class TicketPublisher {

    private final StringRedisTemplate redisTemplate;
    private final String streamKey;

    public TicketPublisher(
            StringRedisTemplate stringRedisTemplate,
            @Value("${app.redis.ticket.stream-key}") String streamKey) {
        this.redisTemplate = stringRedisTemplate;
        this.streamKey = streamKey;
    }

    public void publish(TicketDomain ticketDomain) {
        // 도메인 객체에서 필요한 데이터만 맵으로 추출
        Map<String, String> ticketData = extractTicketData(ticketDomain);

        // 스트림에 추가
        redisTemplate.opsForStream().add(streamKey, ticketData);
    }

    private Map<String, String> extractTicketData(TicketDomain ticketDomain) {
        Map<String, String> ticketData = new HashMap<>();
        ticketData.put("ticketId", ticketDomain.getId().getValue());
        ticketData.put("userId", ticketDomain.getUserId().getValue());
        ticketData.put("createdAt", ticketDomain.getCreatedAt().toString()); // ISO 형식으로 문자열 변환
        ticketData.put("isUsed", String.valueOf(ticketDomain.isUsed()));

        ticketData.put("eventId", String.valueOf(ticketDomain.getEventId()));
        ticketData.put("usedAt", ticketDomain.getUsedAt() != null ? ticketDomain.getUsedAt().toString() : null);
        ticketData.put("expiryDate", ticketDomain.getExpiryDate() != null ? ticketDomain.getExpiryDate().toString() : null);
        ticketData.put("isExpired", String.valueOf(ticketDomain.getIsExpired()));
        
        return ticketData;
    }
}
