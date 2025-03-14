package com.seob.systeminfra.ticket.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DlqProcessor {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TicketConsumer ticketConsumer;

    private static final String DLQ_STREAM = "ticket_stream_dlq";
    private static final String DLQ_GROUP = "dlq_group";
    private static final String DLQ_CONSUMER = "dlq_consumer";


    @Scheduled(fixedDelay = 60000) // 60초마다 실행
    public void processDlqMessages() {
        log.info("DLQ 처리 스케줄러 실행...");

        try {
            // 1) DLQ 메시지 읽기
            List<MapRecord<String, Object, Object>> messages = readDlqMessages(10, 5);

            if (messages == null || messages.isEmpty()) {
                log.info("DLQ에 처리할 메시지가 없습니다.");
                return;
            }

            log.info("DLQ에서 {} 개의 메시지를 처리합니다.", messages.size());

            // 2) 메시지 반복 처리
            for (MapRecord<String, Object, Object> message : messages) {
                processSingleMessage(message);
            }
        } catch (Exception e) {
            log.error("DLQ 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * DLQ에서 메시지를 읽어온다.
     *
     * @param count        최대 읽을 메시지 개수
     * @param blockSeconds 블로킹 대기 시간(초)
     * @return 읽어온 메시지 리스트
     */
    private List<MapRecord<String, Object, Object>> readDlqMessages(long count, long blockSeconds) {
        return redisTemplate
                .opsForStream()
                .read(
                        Consumer.from(DLQ_GROUP, DLQ_CONSUMER),
                        StreamReadOptions.empty().count(count).block(Duration.ofSeconds(blockSeconds)),
                        StreamOffset.create(DLQ_STREAM, ReadOffset.lastConsumed())
                );
    }

    /**
     * 각각의 메시지를 재처리하고 ACK까지 수행.
     */
    private void processSingleMessage(MapRecord<String, Object, Object> message) {
        try {
            log.info("DLQ 메시지 처리 시작: {}", message.getId());
            log.debug("메시지 ID: {}, 데이터: {}", message.getId(), message.getValue());

            // 변환
            MapRecord<String, String, String> stringRecord = convertToStringRecord(message);

            // TicketConsumer를 통해 재처리
            ticketConsumer.onMessage(stringRecord);
            log.info("DLQ 메시지 {} 재처리 성공", message.getId());

        } catch (Exception e) {
            log.error("DLQ 메시지 {} 처리 실패: {}", message.getId(), e.getMessage(), e);
            // 알림 전송, 메트릭 증가 등 추가 오류 처리 로직
        } finally {
            // 처리 후 ACK
            acknowledgeMessage(message);
        }
    }

    /**
     * MapRecord<String, Object, Object>를
     * MapRecord<String, String, String>로 변환한다.
     */
    private MapRecord<String, String, String> convertToStringRecord(MapRecord<String, Object, Object> message) {
        Map<String, String> stringMap = new HashMap<>();
        message.getValue().forEach((k, v) -> stringMap.put(String.valueOf(k), String.valueOf(v)));

        return StreamRecords.newRecord()
                .in(message.getStream())
                .withId(message.getId())
                .ofMap(stringMap);
    }

    /**
     * 메시지를 ACK 처리한다.
     */
    private void acknowledgeMessage(MapRecord<String, Object, Object> message) {
        try {
            redisTemplate.opsForStream().acknowledge(DLQ_GROUP, message);
            log.debug("DLQ 메시지 {} ACK 완료", message.getId());
        } catch (Exception e) {
            log.error("DLQ 메시지 {} ACK 실패: {}", message.getId(), e.getMessage());
        }
    }
}