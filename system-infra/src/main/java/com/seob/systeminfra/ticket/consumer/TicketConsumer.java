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
            StringRedisTemplate stringRedisTemplate) {
        this.ticketRepository = ticketRepository;
        this.redisTemplate = stringRedisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            // 1) 메시지 -> TicketDomain 변환
            TicketDomain ticket = convertToTicketDomain(message.getValue());

            // 2) DB 저장
            ticketRepository.save(ticket);
            // 3) 메시지 성공 처리(Ack)
            acknowledgeMessage(message);

        } catch (Exception e) {
            log.error("메시지 처리 오류 발생 (ID: {}): {}", message.getId(), e.getMessage());
            // 4) 실패 시 재시도 혹은 DLQ로 이동
            try {
                handleFailedMessage(message, e);
            } catch (Exception ex) {
                log.error("실패 처리 중 추가 오류 발생: {}", ex.getMessage());
                // 최악의 경우에도 메시지를 acknowledge하여 스트림에서 제거
                acknowledgeMessage(message);
                // DLQ로 최선의 시도를 한다
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

    /**
     * Map<String, String> 데이터를 TicketDomain으로 파싱한다.
     */
    private TicketDomain convertToTicketDomain(Map<String, String> ticketData) {
        try {
            String ticketIdStr = ticketData.get("ticketId");
            String userIdStr = ticketData.get("userId");
            String createdAtStr = ticketData.get("createdAt");
            String isUsedStr = ticketData.get("isUsed");
            
            // 새 필드 파싱
            String eventIdStr = ticketData.get("eventId");
            String usedAtStr = ticketData.get("usedAt");
            String expiryDateStr = ticketData.get("expiryDate");
            String isExpiredStr = ticketData.get("isExpired");

            UserId userId = UserId.of(userIdStr);
            LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);
            boolean isUsed = Boolean.parseBoolean(isUsedStr);
            
            // null이 아닌 경우만 변환
            Long eventId = eventIdStr != null && !eventIdStr.equals("null") ? Long.parseLong(eventIdStr) : null;
            LocalDateTime usedAt = usedAtStr != null && !usedAtStr.equals("null") && !usedAtStr.isEmpty() ? LocalDateTime.parse(usedAtStr) : null;
            LocalDateTime expiryDate = expiryDateStr != null && !expiryDateStr.equals("null") ? LocalDateTime.parse(expiryDateStr) : null;
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

    /**
     * 메시지를 Ack하여 pending 목록에서 제거한다.
     */
    private void acknowledgeMessage(MapRecord<String, String, String> message) {
        try {
            redisTemplate.opsForStream().acknowledge(groupName, message);
        } catch (Exception e) {
            log.error("메시지 Ack 처리 실패: {}", e.getMessage());
        }
    }

    /**
     * 메시지 처리 실패 시 재시도/ DLQ 이동을 처리한다.
     */
    private void handleFailedMessage(MapRecord<String, String, String> message, Exception e) {
        // 재시도 횟수 확인
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
            // 데이터 정제 및 재시도
            Map<String, String> cleanData = new HashMap<>(message.getValue());
            cleanData.put("retryCount", String.valueOf(retryCount + 1));

            // 스트림에 추가 (재시도)
            redisTemplate.opsForStream().add(streamKey, cleanData);
            log.warn("메시지 {} 재처리 요청 (재시도 {}번째)", message.getId(), retryCount + 1);
        } else {
            // 최대 재시도 횟수 초과 -> DLQ로 이동
            redisTemplate.opsForStream().add(dlqStreamKey, message.getValue());
            log.error("메시지 {} 처리 실패 - DLQ로 이동 (시도 횟수 {}회)", message.getId(), retryCount + 1);
        }

        // PENDING 제거
        acknowledgeMessage(message);
    }
}
