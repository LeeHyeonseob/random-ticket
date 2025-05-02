package com.seob.application.ticket;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systeminfra.ticket.consumer.DlqProcessor;
import com.seob.systeminfra.ticket.consumer.TicketConsumer;
import com.seob.systeminfra.ticket.redis.TicketPublisher;
import com.seob.systeminfra.ticket.service.RedisTicketService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled("CI/CD 파이프라인 설정 전까지 비활성화")
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
        Long eventId = 1L; // 테스트용 이벤트 ID 추가
        
        TicketDomain ticket = redisTicketService.issueTicket(userId, eventId);

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
        int threadCount = 30;
        int requestCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(requestCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> userIds = new ArrayList<>();
        Long eventId = 1L; // 테스트용 이벤트 ID 추가

        // 여러 스레드에서 동시에 티켓 발급 요청
        for (int i = 0; i < requestCount; i++) {
            final String userId = "user" + i;
            userIds.add(userId);

            executorService.submit(() -> {
                try {
                    startLatch.await();

                    try {
                        redisTicketService.issueTicket(UserId.of(userId), eventId);
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

        // 모든 요청 완료 대기
        boolean completed = completionLatch.await(60, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed, "모든 요청이 시간 내에 완료되지 않았습니다");

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
        Long eventId = 1L; // 테스트용 이벤트 ID 추가

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
            int threadCount = 30;
            int requestCount = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completionLatch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // 여러 스레드에서 동시에 요청
            for (int i = 0; i < requestCount; i++) {
                final String userId = "delayedUser" + i;
                executorService.submit(() -> {
                    try {
                        startLatch.await();

                        try {
                            redisTicketService.issueTicket(UserId.of(userId), eventId);
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
            boolean completed = completionLatch.await(60, TimeUnit.SECONDS);
            executorService.shutdown();

            assertTrue(completed, "모든 요청이 시간 내에 완료되지 않았습니다");

            // 결과 확인 (타입 안전하게)
            Number setSize = redisTemplate.opsForSet().size(TICKET_ISSUED_SET);
            Number counterValue = (Number) redisTemplate.opsForValue().get(TICKET_COUNTER_KEY);

            System.out.println("지연 테스트 - 성공 요청 수: " + successCount.get());
            System.out.println("지연 테스트 - 실패 요청 수: " + failureCount.get());
            System.out.println("지연 테스트 - Redis Set 크기: " + setSize);
            System.out.println("지연 테스트 - Redis 카운터 값: " + counterValue);

        } finally {
            // 설정 복원
            reset(ticketPublisher);
        }
    }

    @Test
    public void testConcurrentRollbackFailures() throws InterruptedException {
        // TicketPublisher 모킹을 재설정
        reset(ticketPublisher);
        Long eventId = 1L; // 테스트용 이벤트 ID 추가

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
            int threadCount = 30;
            int requestCount = 300;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completionLatch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // 여러 스레드에서 동시에 티켓 발급 요청
            for (int i = 0; i < requestCount; i++) {
                final String userId = "concurrentRollbackUser" + i;
                executorService.submit(() -> {
                    try {
                        startLatch.await();

                        try {
                            redisTicketService.issueTicket(UserId.of(userId), eventId);
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
            boolean completed = completionLatch.await(60, TimeUnit.SECONDS);
            executorService.shutdown();

            assertTrue(completed, "모든 요청이 시간 내에 완료되지 않았습니다");

            // 결과 확인 (타입 안전하게)
            Number setSize = redisTemplate.opsForSet().size(TICKET_ISSUED_SET);
            Number counterValue = (Number) redisTemplate.opsForValue().get(TICKET_COUNTER_KEY);

            System.out.println("동시 롤백 테스트 - 성공 요청 수: " + successCount.get());
            System.out.println("동시 롤백 테스트 - 실패 요청 수: " + failureCount.get());
            System.out.println("동시 롤백 테스트 - Redis Set 크기: " + setSize);
            System.out.println("동시 롤백 테스트 - Redis 카운터 값: " + counterValue);

        } finally {
            // 테스트 후 정리
            reset(ticketPublisher);
        }
    }
}