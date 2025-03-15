package com.seob.systeminfra.config;

import com.seob.systeminfra.ticket.consumer.TicketConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Configuration
@Slf4j
public class RedisConfig {


    @Value("${spring.redis.stream.ticket.name}")
    private String ticketStream;

    @Value("${spring.redis.stream.ticket.group}")
    private String ticketGroup;

    @Value("${spring.redis.stream.ticket.consumer}")
    private String ticketConsumerName;

    @Value("${spring.redis.stream.dlq.name}")
    private String dlqStream;

    @Value("${spring.redis.stream.dlq.group}")
    private String dlqGroup;

    @Value("${spring.redis.poll-timeout}")
    private long pollTimeout;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    private void createConsumerGroupIfNotExists(RedisTemplate<String, Object> redisTemplate,
                                                String stream, String group) {
        try {
            redisTemplate.opsForStream().createGroup(stream, ReadOffset.from("0"), group);
            log.info("Consumer group '{}' created for stream '{}'", group, stream);
        } catch (Exception e) {
            // Check if the exception is related to BUSYGROUP (consumer group already exists)
            if (e.getCause() != null &&
                    (e.getCause().getMessage() != null && e.getCause().getMessage().contains("BUSYGROUP") ||
                            e.getCause() instanceof org.redisson.client.RedisBusyException)) {
                log.info("Consumer group '{}' already exists for stream '{}'", group, stream);
            } else {
                throw e;
            }
        }
    }

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> ticketStreamListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisTemplate<String, Object> redisTemplate,
            TicketConsumer ticketConsumer) {

        // Consumer Groups 초기화
        createConsumerGroupIfNotExists(redisTemplate, ticketStream, ticketGroup);
        createConsumerGroupIfNotExists(redisTemplate, dlqStream, dlqGroup);

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofMillis(pollTimeout))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        // Consumer 설정 및 스트림 구독
        container.receiveAutoAck(
                Consumer.from(ticketGroup, ticketConsumerName),
                StreamOffset.create(ticketStream, ReadOffset.lastConsumed()),
                ticketConsumer
        );

        container.start();
        return container;
    }
}