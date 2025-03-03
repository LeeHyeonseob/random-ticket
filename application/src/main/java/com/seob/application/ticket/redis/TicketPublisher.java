package com.seob.application.ticket.redis;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


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
        ObjectRecord<String,TicketDomain> message = ObjectRecord.create(streamKey, ticketDomain);

        redisTemplate.opsForStream().add(message);
    }





}
