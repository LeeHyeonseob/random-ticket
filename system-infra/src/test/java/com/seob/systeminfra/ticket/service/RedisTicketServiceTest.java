package com.seob.systeminfra.ticket.service;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.exception.DuplicateTicketIssuanceException;
import com.seob.systemdomain.ticket.exception.TicketExhaustedException;
import com.seob.systemdomain.ticket.exception.TicketProcessException;
import com.seob.systemdomain.ticket.repository.TicketIssuanceRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systeminfra.ticket.redis.TicketPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisTicketServiceTest {

    @Mock
    private TicketPublisher ticketPublisher;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private TicketIssuanceRepository issuanceRepository;

    @Mock
    private RLock lock;

    @InjectMocks
    private RedisTicketService ticketService;

    @BeforeEach
    void setUp() {
        // 프로퍼티 값 주입
        ReflectionTestUtils.setField(ticketService, "ticketLock", "ticket_lock");
        ReflectionTestUtils.setField(ticketService, "lockWaitTime", 10L);
        ReflectionTestUtils.setField(ticketService, "lockLeaseTime", 5L);
        
        // redissonClient Mock 설정
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
    }

    @Test
    @DisplayName("티켓 발급 성공")
    void issueTicket_Success() throws InterruptedException {
        // given
        UserId userId = UserId.of("user123");
        Long eventId = 1L;
        
        // lock 획득 성공
        given(lock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        
        // 중복 발급 검증 통과
        given(issuanceRepository.hasIssuedTicket(userId)).willReturn(false);
        
        // 발급 수량 검증 통과
        given(issuanceRepository.incrementTicketCount()).willReturn(1L);
        given(issuanceRepository.getMaxTickets()).willReturn(100);
        
        // 이벤트 발행 성공
        doNothing().when(ticketPublisher).publish(any(TicketDomain.class));
        
        // when
        TicketDomain result = ticketService.issueTicket(userId, eventId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getEventId()).isEqualTo(eventId);
        
        // 로직 검증
        verify(issuanceRepository).hasIssuedTicket(userId);
        verify(issuanceRepository).incrementTicketCount();
        verify(issuanceRepository).getMaxTickets();
        verify(ticketPublisher).publish(any(TicketDomain.class));
        verify(issuanceRepository).saveIssuance(userId);
        verify(lock).unlock();
    }

    @Test
    @DisplayName("중복 발급 시 예외 발생")
    void issueTicket_DuplicateIssuance() throws InterruptedException {
        // given
        UserId userId = UserId.of("user123");
        Long eventId = 1L;
        
        // lock 획득 성공
        given(lock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        
        // 중복 발급 검증 실패
        given(issuanceRepository.hasIssuedTicket(userId)).willReturn(true);
        
        // when & then
        assertThrows(DuplicateTicketIssuanceException.class, () -> ticketService.issueTicket(userId, eventId));
        
        // 로직 검증
        verify(issuanceRepository).hasIssuedTicket(userId);
        verify(issuanceRepository, never()).incrementTicketCount();
        verify(ticketPublisher, never()).publish(any(TicketDomain.class));
        verify(lock).unlock();
    }

    @Test
    @DisplayName("티켓 소진 시 예외 발생")
    void issueTicket_TicketExhausted() throws InterruptedException {
        // given
        UserId userId = UserId.of("user123");
        Long eventId = 1L;
        
        // lock 획득 성공
        given(lock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        
        // 중복 발급 검증 통과
        given(issuanceRepository.hasIssuedTicket(userId)).willReturn(false);
        
        // 발급 수량 초과
        given(issuanceRepository.incrementTicketCount()).willReturn(101L);
        given(issuanceRepository.getMaxTickets()).willReturn(100);
        
        // when & then
        assertThrows(TicketExhaustedException.class, () -> ticketService.issueTicket(userId, eventId));
        
        // 로직 검증
        verify(issuanceRepository).hasIssuedTicket(userId);
        verify(issuanceRepository).incrementTicketCount();
        verify(issuanceRepository).decrementTicketCount();
        verify(ticketPublisher, never()).publish(any(TicketDomain.class));
        verify(lock).unlock();
    }

    @Test
    @DisplayName("이벤트 발행 실패 시 롤백")
    void issueTicket_PublishFailure() throws InterruptedException {
        // given
        UserId userId = UserId.of("user123");
        Long eventId = 1L;
        
        // lock 획득 성공
        given(lock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        
        // 중복 발급 검증 통과
        given(issuanceRepository.hasIssuedTicket(userId)).willReturn(false);
        
        // 발급 수량 검증 통과
        given(issuanceRepository.incrementTicketCount()).willReturn(1L);
        given(issuanceRepository.getMaxTickets()).willReturn(100);
        
        // 이벤트 발행 실패
        doThrow(new RuntimeException("Publish failed")).when(ticketPublisher).publish(any(TicketDomain.class));
        
        // when & then
        assertThrows(Exception.class, () -> ticketService.issueTicket(userId, eventId));
        
        // 롤백 검증
        verify(issuanceRepository).decrementTicketCount();
        verify(issuanceRepository).cancelIssuance(userId);
        verify(lock).unlock();
    }

    @Test
    @DisplayName("락 획득 실패 시 예외 발생")
    void issueTicket_LockFailure() throws InterruptedException {
        // given
        UserId userId = UserId.of("user123");
        Long eventId = 1L;
        
        // lock 획득 실패
        given(lock.tryLock(anyLong(), anyLong(), any())).willReturn(false);
        
        // when & then
        assertThrows(Exception.class, () -> ticketService.issueTicket(userId, eventId));
        
        // 로직 검증
        verify(issuanceRepository, never()).hasIssuedTicket(any());
        verify(issuanceRepository, never()).incrementTicketCount();
        verify(ticketPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("락 획득 중 인터럽트 발생 시 예외 처리")
    void issueTicket_InterruptedException() throws InterruptedException {
        // given
        UserId userId = UserId.of("user123");
        Long eventId = 1L;
        
        // lock 획득 중 인터럽트 발생
        given(lock.tryLock(anyLong(), anyLong(), any())).willThrow(new InterruptedException());
        
        // when & then
        assertThrows(TicketProcessException.class, () -> ticketService.issueTicket(userId, eventId));
        
        // 로직 검증
        verify(issuanceRepository, never()).hasIssuedTicket(any());
        verify(issuanceRepository, never()).incrementTicketCount();
        verify(ticketPublisher, never()).publish(any());
    }
}
