package com.seob.systeminfra.config;

import io.lettuce.core.RedisBusyException;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisConsumerGroupInitializer {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisConsumerGroupInitializer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        // Initialize ticket_stream and ticket_group
        String ticketStreamKey = "ticket_stream";
        String ticketGroupName = "ticket_group";
        try {
            redisTemplate.opsForStream().createGroup(ticketStreamKey, ReadOffset.from("0"), ticketGroupName);
        } catch (RedisSystemException e) {
            if (e.getRootCause() instanceof RedisBusyException) {
                // 이미 그룹이 존재하는 경우 -> 예외 무시
            } else {
                throw e;
            }
        }

        // Initialize ticket_stream_dlq and dlq_group
        String dlqStreamKey = "ticket_stream_dlq";
        String dlqGroupName = "dlq_group";
        try {
            redisTemplate.opsForStream().createGroup(dlqStreamKey, ReadOffset.from("0"), dlqGroupName);
        } catch (RedisSystemException e) {
            if (e.getRootCause() instanceof RedisBusyException) {
                // 이미 그룹이 존재하는 경우 -> 예외 무시
            } else {
                throw e;
            }
        }
    }
}
