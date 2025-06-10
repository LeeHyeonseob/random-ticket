package com.seob.systeminfra.ticket.consumer;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TicketConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final TicketRepository ticketRepository;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.redis.stream.ticket.name}")
    private String streamKey;

    @Value("${app.redis.stream.ticket.group}")
    private String groupName;

    @Value("${app.redis.stream.dlq.name}")
    private String dlqStreamKey;

    @Value("${app.redis.max-retry:3}")
    private int maxRetry;

    public TicketConsumer(
            TicketRepository ticketRepository,
            StringRedisTemplate stringRedisTemplate
    ) {
        this.ticketRepository = ticketRepository;
        this.redisTemplate = stringRedisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            TicketDomain ticket = convertToTicketDomain(message.getValue());
            ticketRepository.save(ticket);
            acknowledgeMessage(message);
        } catch (Exception e) {
            log.error("메시지 처리 오류 발생 (ID: {}): {}", message.getId(), e.getMessage());
            try {
                handleFailedMessage(message, e);
            } catch (Exception ex) {
                log.error("실패 처리 중 추가 오류 발생: {}", ex.getMessage());
                acknowledgeMessage(message);
                try {
                    Map<String, String> cleanedData = new HashMap<>(message.getValue());
                    redisTemplate.opsForStream().add(dlqStreamKey, cleanedData);
                    log.warn("메시지 {} DLQ로 이동 완료", message.getId());
                } catch (Exception dlqEx) {
                    log.error("DLQ 이동 중 오류: {}", dlqEx.getMessage());
                }
            }
        }
    }

    private TicketDomain convertToTicketDomain(Map<String, String> ticketData) {
        try {
            String ticketIdStr = ticketData.get("ticketId");
            String userIdStr = ticketData.get("userId");
            String createdAtStr = ticketData.get("createdAt");
            String isUsedStr = ticketData.get("isUsed");
            String eventIdStr = ticketData.get("eventId");
            String usedAtStr = ticketData.get("usedAt");
            String expiryDateStr = ticketData.get("expiryDate");
            String isExpiredStr = ticketData.get("isExpired");

            UserId userId = UserId.of(userIdStr);
            LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);
            boolean isUsed = Boolean.parseBoolean(isUsedStr);

            Long eventId = eventIdStr != null && !eventIdStr.equals("null")
                    ? Long.parseLong(eventIdStr) : null;

            LocalDateTime usedAt = usedAtStr != null && !usedAtStr.equals("null") && !usedAtStr.isEmpty()
                    ? LocalDateTime.parse(usedAtStr) : null;

            LocalDateTime expiryDate = expiryDateStr != null && !expiryDateStr.equals("null")
                    ? LocalDateTime.parse(expiryDateStr) : null;

            boolean isExpired = isExpiredStr != null ? Boolean.parseBoolean(isExpiredStr) : false;

            return TicketDomain.of(
                    ticketIdStr,
                    userId,
                    eventId,
                    createdAt,
                    usedAt,
                    expiryDate,
                    isUsed,
                    isExpired
            );
        } catch (DateTimeParseException e) {
            log.error("날짜 파싱 오류: {}", e.getMessage());
            throw new IllegalArgumentException("날짜 형식 오류: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("티켓 도메인 변환 오류: {}", e.getMessage());
            throw new IllegalArgumentException("티켓 도메인 변환 실패: " + e.getMessage(), e);
        }
    }

    private void acknowledgeMessage(MapRecord<String, String, String> message) {
        try {
            redisTemplate.opsForStream().acknowledge(groupName, message);
        } catch (Exception e) {
            log.error("메시지 Ack 처리 실패: {}", e.getMessage());
        }
    }

    private void handleFailedMessage(MapRecord<String, String, String> message, Exception e) {
        String retryCountStr = message.getValue().get("retryCount");
        int retryCount = 0;

        if (retryCountStr != null) {
            try {
                retryCount = Integer.parseInt(retryCountStr);
            } catch (NumberFormatException ex) {
                log.warn("재시도 횟수 파싱 오류, 0으로 기본값 설정: {}", ex.getMessage());
            }
        }

        if (retryCount < maxRetry - 1) {
            Map<String, String> cleanData = new HashMap<>(message.getValue());
            cleanData.put("retryCount", String.valueOf(retryCount + 1));
            redisTemplate.opsForStream().add(streamKey, cleanData);
            log.warn("메시지 {} 재처리 요청 (재시도 {}번째)", message.getId(), retryCount + 1);
        } else {
            redisTemplate.opsForStream().add(dlqStreamKey, message.getValue());
            log.error("메시지 {} 처리 실패 - DLQ로 이동 (시도 횟수 {}회)", message.getId(), retryCount + 1);
        }
        acknowledgeMessage(message);
    }
}