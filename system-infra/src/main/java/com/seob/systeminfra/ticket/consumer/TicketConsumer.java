package com.seob.systeminfra.ticket.consumer;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class TicketConsumer implements StreamListener<String, MapRecord<String, String,String>> {

    private final TicketRepository ticketRepository;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            // MapRecord에서 데이터 추출
            Map<String, String> ticketData = message.getValue();

            // 도메인 객체 재구성
            String ticketIdStr = ticketData.get("ticketId");
            String userIdStr = ticketData.get("userId");
            String createdAtStr = ticketData.get("createdAt");
            String isUsedStr = ticketData.get("isUsed");

            //따옴표 제거
            ticketIdStr = removeQuotes(ticketIdStr);
            userIdStr = removeQuotes(userIdStr);
            createdAtStr = removeQuotes(createdAtStr);



            // 문자열 데이터를 적절한 타입으로 변환
            UserId userId = UserId.of(userIdStr);
            LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);
            Boolean isUsed = Boolean.parseBoolean(isUsedStr);

            // 티켓 도메인 생성
            TicketDomain ticket = TicketDomain.of(ticketIdStr, userId, createdAt, isUsed);

            // DB 저장 등 필요한 처리
            ticketRepository.save(ticket);

            // 메시지 처리 확인은 따로 필요 없음
            // acknowledge 메서드는 Consumer Group 모드에서만 사용 가능
        } catch (Exception e) {
            // 오류 로깅
            System.err.println("메시지 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String removeQuotes(String value) {
        if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}