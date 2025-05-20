package com.seob.application.winner.service;

import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WinnerFacadeServiceTest {

    @Mock
    private WinnerApplicationService winnerApplicationService;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private WinnerFacadeService winnerFacadeService;

    @Test
    @DisplayName("이벤트 당첨자 선정 및 보상 발송 테스트 - 성공 케이스")
    void processEventWinnerWithRewardSuccessTest() {
        // given
        Long eventId = 1L;
        Long winnerId = 10L;
        
        WinnerDomain winnerDomain = mock(WinnerDomain.class);
        when(winnerDomain.getId()).thenReturn(winnerId);
        
        when(winnerApplicationService.selectWinner(eventId)).thenReturn(winnerDomain);
        when(winnerApplicationService.sendReward(winnerId)).thenReturn(true);

        // when
        boolean result = winnerFacadeService.processEventWinnerWithReward(eventId);

        // then
        assertThat(result).isTrue();
        verify(winnerApplicationService).selectWinner(eventId);
        verify(winnerApplicationService).sendReward(winnerId);
    }

    @Test
    @DisplayName("이벤트 당첨자 선정 및 보상 발송 테스트 - 보상 발송 실패")
    void processEventWinnerWithRewardFailureTest() {
        // given
        Long eventId = 1L;
        Long winnerId = 10L;
        
        WinnerDomain winnerDomain = mock(WinnerDomain.class);
        when(winnerDomain.getId()).thenReturn(winnerId);
        
        when(winnerApplicationService.selectWinner(eventId)).thenReturn(winnerDomain);
        when(winnerApplicationService.sendReward(winnerId)).thenReturn(false);

        // when
        boolean result = winnerFacadeService.processEventWinnerWithReward(eventId);

        // then
        assertThat(result).isFalse();
        verify(winnerApplicationService).selectWinner(eventId);
        verify(winnerApplicationService).sendReward(winnerId);
    }

    @Test
    @DisplayName("어제 종료된 이벤트 처리 테스트")
    void processYesterdayEventTest() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Long yesterdayEventId = 1L;
        
        when(eventRepository.findIdByDate(yesterday)).thenReturn(yesterdayEventId);
        
        WinnerDomain winnerDomain = mock(WinnerDomain.class);
        when(winnerDomain.getId()).thenReturn(10L);
        
        when(winnerApplicationService.selectWinner(yesterdayEventId)).thenReturn(winnerDomain);
        when(winnerApplicationService.sendReward(10L)).thenReturn(true);

        // when
        winnerFacadeService.processYesterdayEvent();

        // then
        verify(eventRepository).findIdByDate(yesterday);
        verify(winnerApplicationService).selectWinner(yesterdayEventId);
        verify(winnerApplicationService).sendReward(10L);
    }

    @Test
    @DisplayName("어제 종료된 이벤트 처리 테스트 - 이벤트 없음")
    void processYesterdayEventNoEventTest() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(eventRepository.findIdByDate(yesterday)).thenReturn(null);

        // when
        winnerFacadeService.processYesterdayEvent();

        // then
        verify(eventRepository).findIdByDate(yesterday);
        verify(winnerApplicationService, never()).selectWinner(anyLong());
        verify(winnerApplicationService, never()).sendReward(anyLong());
    }

    @Test
    @DisplayName("어제 종료된 이벤트 처리 테스트 - 예외 발생")
    void processYesterdayEventExceptionTest() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Long yesterdayEventId = 1L;
        
        when(eventRepository.findIdByDate(yesterday)).thenReturn(yesterdayEventId);
        when(winnerApplicationService.selectWinner(yesterdayEventId))
            .thenThrow(new RuntimeException("당첨자 선정 실패"));

        // when
        winnerFacadeService.processYesterdayEvent(); // 예외가 발생해도 메서드가 완료되어야 함

        // then
        verify(eventRepository).findIdByDate(yesterday);
        verify(winnerApplicationService).selectWinner(yesterdayEventId);
        verify(winnerApplicationService, never()).sendReward(anyLong());
    }

    @Test
    @DisplayName("실패한 보상 재시도 테스트 - 성공")
    void retryFailedRewardsSuccessTest() {
        // given
        WinnerRewardDetailInfo winner1 = createWinnerRewardDetailInfo(1L);
        WinnerRewardDetailInfo winner2 = createWinnerRewardDetailInfo(2L);
        List<WinnerRewardDetailInfo> failedWinners = Arrays.asList(winner1, winner2);
        
        when(winnerApplicationService.getWinnersByStatus(RewardStatus.FAILED)).thenReturn(failedWinners);
        when(winnerApplicationService.sendReward(1L)).thenReturn(true);
        when(winnerApplicationService.sendReward(2L)).thenReturn(true);

        // when
        int result = winnerFacadeService.retryFailedRewards();

        // then
        assertThat(result).isEqualTo(2);
        verify(winnerApplicationService).getWinnersByStatus(RewardStatus.FAILED);
        verify(winnerApplicationService).sendReward(1L);
        verify(winnerApplicationService).sendReward(2L);
    }

    @Test
    @DisplayName("실패한 보상 재시도 테스트 - 부분 성공")
    void retryFailedRewardsPartialSuccessTest() {
        // given
        WinnerRewardDetailInfo winner1 = createWinnerRewardDetailInfo(1L);
        WinnerRewardDetailInfo winner2 = createWinnerRewardDetailInfo(2L);
        List<WinnerRewardDetailInfo> failedWinners = Arrays.asList(winner1, winner2);
        
        when(winnerApplicationService.getWinnersByStatus(RewardStatus.FAILED)).thenReturn(failedWinners);
        when(winnerApplicationService.sendReward(1L)).thenReturn(true);
        when(winnerApplicationService.sendReward(2L)).thenReturn(false);

        // when
        int result = winnerFacadeService.retryFailedRewards();

        // then
        assertThat(result).isEqualTo(1);
        verify(winnerApplicationService).getWinnersByStatus(RewardStatus.FAILED);
        verify(winnerApplicationService).sendReward(1L);
        verify(winnerApplicationService).sendReward(2L);
    }

    @Test
    @DisplayName("실패한 보상 재시도 테스트 - 실패 건 없음")
    void retryFailedRewardsNoFailuresTest() {
        // given
        when(winnerApplicationService.getWinnersByStatus(RewardStatus.FAILED)).thenReturn(Collections.emptyList());

        // when
        int result = winnerFacadeService.retryFailedRewards();

        // then
        assertThat(result).isEqualTo(0);
        verify(winnerApplicationService).getWinnersByStatus(RewardStatus.FAILED);
        verify(winnerApplicationService, never()).sendReward(anyLong());
    }

    @Test
    @DisplayName("실패한 보상 재시도 테스트 - 예외 발생")
    void retryFailedRewardsExceptionTest() {
        // given
        WinnerRewardDetailInfo winner1 = createWinnerRewardDetailInfo(1L);
        WinnerRewardDetailInfo winner2 = createWinnerRewardDetailInfo(2L);
        List<WinnerRewardDetailInfo> failedWinners = Arrays.asList(winner1, winner2);
        
        when(winnerApplicationService.getWinnersByStatus(RewardStatus.FAILED)).thenReturn(failedWinners);
        when(winnerApplicationService.sendReward(1L)).thenThrow(new RuntimeException("발송 실패"));
        when(winnerApplicationService.sendReward(2L)).thenReturn(true);

        // when
        int result = winnerFacadeService.retryFailedRewards();

        // then
        assertThat(result).isEqualTo(1); // 예외가 발생해도 다음 항목 처리
        verify(winnerApplicationService).getWinnersByStatus(RewardStatus.FAILED);
        verify(winnerApplicationService).sendReward(1L);
        verify(winnerApplicationService).sendReward(2L);
    }
    
    // 테스트용 헬퍼 메서드
    private WinnerRewardDetailInfo createWinnerRewardDetailInfo(Long winnerId) {
        return WinnerRewardDetailInfo.of(
            winnerId, "user-" + winnerId, "User " + winnerId, "user" + winnerId + "@example.com",
            100L + winnerId, "Event " + winnerId, "Event Description " + winnerId,
            200L + winnerId, "Reward " + winnerId, RewardStatus.FAILED, null
        );
    }
}
