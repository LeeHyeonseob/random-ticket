package com.seob.application.ticket;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systeminfra.ticket.consumer.DlqProcessor;
import com.seob.systeminfra.ticket.consumer.TicketConsumer;
import com.seob.systeminfra.ticket.exception.PublishFailureException;
import com.seob.systeminfra.ticket.redis.TicketPublisher;
import com.seob.systeminfra.ticket.service.RedisTicketService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 테스트 후 DB 자동 롤백
public class RedisTicketServiceConcurrencyTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @MockitoBean
    private TicketPublisher ticketPublisher;

    @MockitoSpyBean
    private TicketConsumer ticketConsumer;

    @Autowired
    private DlqProcessor dlqProcessor;

    @Autowired
    private RedisTicketService redisTicketService;

    // Redis 키들
    private static final String TICKET_ISSUED_SET = "issued_tickets";
    private static final String TICKET_COUNTER_KEY = "ticket_counter";
    private static final String DLQ_STREAM = "ticket_stream_dlq";
    private static final String TICKET_STREAM = "ticket_stream_test";

    @BeforeEach
    void setUp() {
        redisTemplate.delete(TICKET_ISSUED_SET);
        redisTemplate.delete(TICKET_COUNTER_KEY);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(DLQ_STREAM))) {
            try {
                redisTemplate.opsForStream().destroyGroup(DLQ_STREAM, "dlq_group");
            } catch (Exception e) {
                // 그룹이 존재하지 않거나 삭제에 실패하면 무시
            }
        }
        redisTemplate.delete(DLQ_STREAM);

        redisTemplate.delete(TICKET_STREAM);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete(TICKET_ISSUED_SET);
        redisTemplate.delete(TICKET_COUNTER_KEY);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(DLQ_STREAM))) {
            try {
                redisTemplate.opsForStream().destroyGroup(DLQ_STREAM, "dlq_group");
            } catch (Exception e) {
                // 그룹이 존재하지 않거나 삭제에 실패하면 무시
            }
        }
        redisTemplate.delete(DLQ_STREAM);

        redisTemplate.delete(TICKET_STREAM);
    }

    @Test
    @DisplayName("단일 티켓 발급")
    public void testBasicTicketIssuance() {
        // 기본 기능 테스트
        UserId userId = UserId.of("testUser");
        TicketDomain ticket = redisTicketService.issueTicket(userId);

        // 검증
        assertNotNull(ticket);
        assertTrue(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(TICKET_ISSUED_SET, userId.getValue())));

        // 타입 불일치 해결 - Number 타입으로 비교
        Number counterValue = (Number) redisTemplate.opsForValue().get(TICKET_COUNTER_KEY);
        assertEquals(1, counterValue.intValue());
    }

    @Test
    @DisplayName("동시 티켓 발급")
    public void testConcurrentTicketIssuance() throws InterruptedException {
        // 동시성 테스트 설정
        int threadCount = 300;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> userIds = new ArrayList<>();

        // 여러 스레드에서 동시에 티켓 발급 요청
        for (int i = 0; i < threadCount; i++) {
            final String userId = "user" + i;
            userIds.add(userId);

            executorService.submit(() -> {
                try {
                    startLatch.await();

                    try {
                        redisTicketService.issueTicket(UserId.of(userId));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        System.err.println("Error issuing ticket for " + userId + ": " + e.getMessage());
                        failureCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        // 모든 스레드 동시 시작
        startLatch.countDown();

        // 모든 스레드 완료 대기
        boolean completed = completionLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed, "모든 스레드가 시간 내에 완료되지 않았습니다");

        // 결과 확인 - 타입 안전한 비교
        Number setSize = redisTemplate.opsForSet().size(TICKET_ISSUED_SET);
        Number counterValue = (Number) redisTemplate.opsForValue().get(TICKET_COUNTER_KEY);

        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failureCount.get());
        System.out.println("Redis Set 크기: " + setSize);
        System.out.println("Redis 카운터 값: " + counterValue);

        // 성공한 요청 수와 Set 크기가 같아야 함 (타입 안전한 비교)
        assertEquals(successCount.get(), setSize.intValue(),
                "성공한 요청 수와 Set에 저장된 사용자 수가 일치해야 합니다");

        // Set 크기와 카운터 값이 같아야 함 (타입 안전한 비교)
        assertEquals(setSize.intValue(), counterValue.intValue(),
                "Set 크기와 카운터 값이 일치해야 합니다");
    }

    @Test
    public void demonstrateConcurrencyIssueWithDelay() throws InterruptedException {
        // 원래 서비스를 저장
        RedisTicketService originalService = redisTicketService;

        // TicketPublisher 모킹을 재설정 (이미 @MockBean으로 주입됨)
        reset(ticketPublisher);

        // 인위적 지연을 추가
        doAnswer(invocation -> {
            TicketDomain ticket = invocation.getArgument(0);
            // 50% 확률로 지연 추가
            if (Math.random() < 0.5) {
                Thread.sleep(200); // 지연 추가
            }
            return null; // void 메서드이므로 null 반환
        }).when(ticketPublisher).publish(any(TicketDomain.class));

        try {
            // 테스트 설정
            int threadCount = 300;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completionLatch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // 여러 스레드에서 동시에
            for (int i = 0; i < threadCount; i++) {
                final String userId = "delayedUser" + i;
                executorService.submit(() -> {
                    try {
                        startLatch.await();

                        try {
                            redisTicketService.issueTicket(UserId.of(userId));
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            System.err.println("Error issuing ticket for " + userId + ": " + e.getMessage());
                            failureCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        completionLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            completionLatch.await(30, TimeUnit.SECONDS);
            executorService.shutdown();

            // 결과 확인 (타입 안전하게)
            Number setSize = redisTemplate.opsForSet().size(TICKET_ISSUED_SET);
            Number counterValue = (Number) redisTemplate.opsForValue().get(TICKET_COUNTER_KEY);

            System.out.println("지연 테스트 - 성공 요청 수: " + successCount.get());
            System.out.println("지연 테스트 - 실패 요청 수: " + failureCount.get());
            System.out.println("지연 테스트 - Redis Set 크기: " + setSize);
            System.out.println("지연 테스트 - Redis 카운터 값: " + counterValue);

            // 불일치가 발생할 가능성 있음
            if (setSize.intValue() != counterValue.intValue() ||
                    successCount.get() != setSize.intValue()) {
                System.out.println("데이터 일관성 문제 발견!");
            }
        } finally {
            // 설정 복원
            reset(ticketPublisher);
        }
    }

    @Test
    public void testConcurrentRollbackFailures() throws InterruptedException {
        // TicketPublisher 모킹을 재설정
        reset(ticketPublisher);

        // 인위적 실패와 롤백 문제를 시뮬레이션
        doAnswer(invocation -> {
            // 50% 확률로 발행 실패
            if (Math.random() < 0.5) {
                throw new RuntimeException("Simulated random publishing failure");
            }
            return null; // void 메서드이므로 null 반환
        }).when(ticketPublisher).publish(any(TicketDomain.class));

        try {
            // 테스트 설정
            int threadCount = 200;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completionLatch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // 여러 스레드에서 동시에 티켓 발급 요청
            for (int i = 0; i < threadCount; i++) {
                final String userId = "concurrentRollbackUser" + i;
                executorService.submit(() -> {
                    try {
                        startLatch.await();

                        try {
                            redisTicketService.issueTicket(UserId.of(userId));
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        completionLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            completionLatch.await(30, TimeUnit.SECONDS);
            executorService.shutdown();

            // 결과 확인 (타입 안전하게)
            Number setSize = redisTemplate.opsForSet().size(TICKET_ISSUED_SET);
            Number counterValue = (Number) redisTemplate.opsForValue().get(TICKET_COUNTER_KEY);

            System.out.println("동시 롤백 테스트 - 성공 요청 수: " + successCount.get());
            System.out.println("동시 롤백 테스트 - 실패 요청 수: " + failureCount.get());
            System.out.println("동시 롤백 테스트 - Redis Set 크기: " + setSize);
            System.out.println("동시 롤백 테스트 - Redis 카운터 값: " + counterValue);

            // 데이터 일관성이 유지되어야 함: 성공한 요청 수, SET 크기, 카운터 값이 모두 동일해야 함.
            assertEquals(successCount.get(), setSize.intValue(), "성공한 요청 수와 SET에 저장된 사용자 수가 일치해야 합니다");
            assertEquals(setSize.intValue(), counterValue.intValue(), "SET 크기와 카운터 값이 일치해야 합니다");
        } finally {
            // 테스트 후 정리
            reset(ticketPublisher);
        }
    }

    // ========= DlqProcessor 테스트 =========

    @Test
    public void testDlqProcessorInitialization() {

        // 스트림 존재 확인
        Boolean streamExists = stringRedisTemplate.hasKey(DLQ_STREAM);
        assertTrue(streamExists, "DLQ 스트림이 생성되어야 합니다");

        // Consumer 그룹 존재 여부 확인 (이미 존재하면 BUSYGROUP 예외)
        try {
            // 그룹이 이미 존재하는지 확인하는 간접적인 방법
            StreamInfo.XInfoGroups groups = stringRedisTemplate.opsForStream().groups(DLQ_STREAM);
            assertFalse(groups.isEmpty(), "DLQ 그룹이 존재해야 합니다");

            // 첫 그룹의 이름이 "dlq_group"인지 확인
            assertEquals("dlq_group", groups.get(0).groupName());
        } catch (Exception e) {
            fail("DLQ 그룹 정보를 가져오는 중 오류 발생: " + e.getMessage());
        }
    }


    @Test
    public void testTypeConversion() throws Exception {
        // 다양한 타입의 데이터로 메시지 생성
        Map<String, Object> mixedData = new HashMap<>();
        mixedData.put("ticketId", 12345);      // 정수
        mixedData.put("userId", "user-789");   // 문자열
        mixedData.put("createdAt", "2025-03-14T10:15:30");
        mixedData.put("isUsed", Boolean.FALSE); // Boolean 객체

        // DLQ에 추가 (Object 타입으로)
        redisTemplate.opsForStream().add(DLQ_STREAM, mixedData);

        // TicketConsumer 모킹
        doNothing().when(ticketConsumer).onMessage(any());

        // DLQ 처리
        dlqProcessor.processDlqMessages();

        // String 타입으로 변환되었는지 확인
        ArgumentCaptor<MapRecord<String, String, String>> messageCaptor =
                ArgumentCaptor.forClass(MapRecord.class);
        verify(ticketConsumer, timeout(5000)).onMessage(messageCaptor.capture());

        // 타입 변환 결과 확인
        MapRecord<String, String, String> capturedMessage = messageCaptor.getValue();
        assertEquals("12345", capturedMessage.getValue().get("ticketId")); // 정수가 문자열로 변환
        assertEquals("user-789", capturedMessage.getValue().get("userId"));
        assertEquals("false", capturedMessage.getValue().get("isUsed")); // Boolean이 문자열로 변환
    }
}