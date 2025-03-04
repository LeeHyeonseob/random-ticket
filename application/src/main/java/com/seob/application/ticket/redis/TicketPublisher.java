package com.seob.application.ticket.redis;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class TicketPublisher {

    private final RedisTemplate<String,Object> redisTemplate;
    private final String streamKey;

    public TicketPublisher(RedisTemplate<String, Object> redisTemplate,
                           @Value("${redis.ticket.stream-key}") String streamKey) {
        this.redisTemplate = redisTemplate;
        this.streamKey = streamKey;
    }

    public void publish(TicketDomain ticketDomain) {
        // 도메인 객체에서 필요한 데이터만 맵으로 추출
        Map<String, String> ticketData = new HashMap<>();
        ticketData.put("ticketId", ticketDomain.getId().getValue());
        ticketData.put("userId", ticketDomain.getUserId().getValue());
        ticketData.put("createdAt", ticketDomain.getCreatedAt().toString()); // ISO 형식으로 문자열 변환
        ticketData.put("isUsed", String.valueOf(ticketDomain.isUsed()));


        // MapRecord 생성 및 스트림에 추가
        MapRecord<String, String, String> record = MapRecord.create(streamKey, ticketData);
        redisTemplate.opsForStream().add(record);
    }





}
