package com.seob.systeminfra.ticket.consumer;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class TicketConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final TicketRepository ticketRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Stream과 Consumer Group 정보
    private static final String STREAM_KEY = "ticket_stream";
    private static final String GROUP_NAME = "ticket_group";
    private static final int MAX_RETRY = 3; // 최대 재시도 횟수

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            // 1) 메시지 -> TicketDomain 변환
            TicketDomain ticket = convertToTicketDomain(message.getValue());

            // 2) DB 저장
            ticketRepository.save(ticket);
            log.info("티켓 발급 처리 완료 - ticketId: {}, userId: {}", ticket.getId().getValue(), ticket.getUserId().getValue());

            // 3) 메시지 성공 처리(Ack)
            acknowledgeMessage(message);

        } catch (Exception e) {
            log.error("메시지 처리 오류 발생 (ID: {}): {}", message.getId(), e.getMessage());
            // 4) 실패 시 재시도 혹은 DLQ로 이동
            handleFailedMessage(message, e);
        }
    }

    /**
     * Map<String, String> 데이터를 TicketDomain으로 파싱한다.
     */
    private TicketDomain convertToTicketDomain(Map<String, String> ticketData) {
        String ticketIdStr = removeQuotes(ticketData.get("ticketId"));
        String userIdStr   = removeQuotes(ticketData.get("userId"));
        String createdAtStr= removeQuotes(ticketData.get("createdAt"));
        String isUsedStr   = removeQuotes(ticketData.get("isUsed"));

        UserId userId = UserId.of(userIdStr);
        LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);
        boolean isUsed = Boolean.parseBoolean(isUsedStr);

        return TicketDomain.of(ticketIdStr, userId, createdAt, isUsed);
    }

    /**
     * 메시지를 Ack하여 pending 목록에서 제거한다.
     */
    private void acknowledgeMessage(MapRecord<String, String, String> message) {
        redisTemplate.opsForStream().acknowledge(GROUP_NAME, message);
    }

    /**
     * 메시지 처리 실패 시 재시도/ DLQ 이동을 처리한다.
     */
    private void handleFailedMessage(MapRecord<String, String, String> message, Exception e) {
        // 재시도 횟수 확인
        String retryCountStr = message.getValue().get("retryCount");
        int retryCount = (retryCountStr == null ? 0 : Integer.parseInt(retryCountStr));

        if (retryCount < MAX_RETRY - 1) {
            // 1) retryCount 증가
            Map<String, String> newData = new HashMap<>(message.getValue());
            newData.put("retryCount", String.valueOf(retryCount + 1));

            // 2) 다시 스트림에 추가 (재시도)
            redisTemplate.opsForStream().add(STREAM_KEY, newData);
            log.warn("메시지 {} 재처리 요청 (재시도 {}번째)", message.getId(), retryCount + 1);
        } else {
            // 최대 재시도 횟수 초과 -> DLQ로 이동
            redisTemplate.opsForStream().add("ticket_stream_dlq", message.getValue());
            log.error("메시지 {} 처리 실패 - DLQ로 이동 (시도 횟수 {}회)", message.getId(), retryCount + 1);
        }

        // PENDING 제거
        acknowledgeMessage(message);
    }

    /**
     * 문자열 앞뒤의 쌍따옴표를 제거해주는 보조 메서드
     */
    private String removeQuotes(String value) {
        if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}