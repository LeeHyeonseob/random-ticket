package com.seob.systeminfra.ticket.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DlqProcessor {

    private final StringRedisTemplate redisTemplate;
    private final TicketConsumer ticketConsumer;

    @Value("${app.redis.stream.dlq.name}")
    private String dlqStream;

    @Value("${app.redis.stream.dlq.group}")
    private String dlqGroup;

    @Value("${app.redis.stream.dlq.consumer:dlq_consumer}")
    private String dlqConsumer;

    @Value("${app.redis.dlq.process-count:10}")
    private int processCount;

    @Value("${app.redis.dlq.block-seconds:5}")
    private int blockSeconds;

    public DlqProcessor(
            StringRedisTemplate stringRedisTemplate,
            TicketConsumer ticketConsumer) {
        this.redisTemplate = stringRedisTemplate;
        this.ticketConsumer = ticketConsumer;
    }

    @Scheduled(fixedDelayString = "${app.redis.dlq.schedule-delay:60000}")
    public void processDlqMessages() {
        log.info("DLQ 처리 스케줄러 실행...");

        try {
            // 1) DLQ 메시지 읽기
            List<MapRecord<String, String, String>> messages = readDlqMessages(processCount, blockSeconds);

            if (messages == null || messages.isEmpty()) {
                log.info("DLQ에 처리할 메시지가 없습니다.");
                return;
            }

            log.info("DLQ에서 {} 개의 메시지를 처리합니다.", messages.size());

            // 2) 메시지 반복 처리
            for (MapRecord<String, String, String> message : messages) {
                processSingleMessage(message);
            }
        } catch (Exception e) {
            log.error("DLQ 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private List<MapRecord<String, String, String>> readDlqMessages(long count, long blockSeconds) {
        // 원본 타입으로 메시지 읽기
        List<MapRecord<String, Object, Object>> rawMessages = redisTemplate
                .opsForStream()
                .read(
                        Consumer.from(dlqGroup, dlqConsumer),
                        StreamReadOptions.empty().count(count).block(Duration.ofSeconds(blockSeconds)),
                        StreamOffset.create(dlqStream, ReadOffset.lastConsumed())
                );

        // String 타입으로 변환
        if (rawMessages == null || rawMessages.isEmpty()) {
            return Collections.emptyList();
        }

        // 읽은 메시지를 String 타입으로 변환
        return rawMessages.stream()
                .map(this::convertToStringRecord)
                .collect(Collectors.toList());
    }

    // MapRecord<String, Object, Object>를 MapRecord<String, String, String>으로 변환
    private MapRecord<String, String, String> convertToStringRecord(MapRecord<String, Object, Object> message) {
        // 필드와 값을 문자열로 변환
        Map<String, String> stringMap = new HashMap<>();
        message.getValue().forEach((k, v) -> stringMap.put(String.valueOf(k), String.valueOf(v)));

        return StreamRecords.newRecord()
                .in(message.getStream())
                .withId(message.getId())
                .ofMap(stringMap);
    }

    // 각각의 메시지를 재처리하고 ACK까지 수행
    private void processSingleMessage(MapRecord<String, String, String> message) {
        try {
            log.info("DLQ 메시지 처리 시작: {}", message.getId());
            log.debug("메시지 ID: {}, 데이터: {}", message.getId(), message.getValue());

            // TicketConsumer를 통해 재처리
            ticketConsumer.onMessage(message);
            log.info("DLQ 메시지 {} 재처리 성공", message.getId());

        } catch (Exception e) {
            log.error("DLQ 메시지 {} 처리 실패: {}", message.getId(), e.getMessage(), e);
        } finally {
            // 처리 후 ACK
            acknowledgeMessage(message);
        }
    }

    // 메시지를 ACK 처리한다
    private void acknowledgeMessage(MapRecord<String, String, String> message) {
        try {
            redisTemplate.opsForStream().acknowledge(dlqGroup, message);
            log.debug("DLQ 메시지 {} ACK 완료", message.getId());
        } catch (Exception e) {
            log.error("DLQ 메시지 {} ACK 실패: {}", message.getId(), e.getMessage());
        }
    }
}
