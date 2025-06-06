package com.seob.application.winner.service;

import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.repository.RewardRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerNotificationInfo;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.exception.WinnerNotFoundException;
import com.seob.systemdomain.winner.exception.WinnerAlreadyProcessedException;
import com.seob.systemdomain.winner.repository.WinnerQueryRepository;
import com.seob.systemdomain.winner.service.WinnerService;
import com.seob.systemdomain.winner.vo.RewardStatus;
import com.seob.systeminfra.email.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WinnerApplicationServiceTest {

    @Mock
    private WinnerService winnerService;

    @Mock
    private WinnerQueryRepository winnerQueryRepository;

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private WinnerApplicationServiceImpl winnerApplicationService;

    @Test
    @DisplayName("당첨자 선정 테스트 - 정상 케이스")
    void selectWinnerSuccessTest() {
        // given
        Long eventId = 1L;
        Long rewardId = 2L;
        String userId = "user-1";
        List<String> participants = Arrays.asList(userId, "user-2", "user-3");
        
        when(entryRepository.findUserIdByEventId(eventId)).thenReturn(participants);
        when(winnerService.existsByEventId(eventId)).thenReturn(false);
        
        RewardDomain rewardDomain = RewardDomain.of(rewardId, eventId, "테스트 보상", "http://example.com/reward", LocalDateTime.now());
        when(rewardRepository.findByEventId(eventId)).thenReturn(Optional.of(rewardDomain));
        
        WinnerDomain expectedWinner = WinnerDomain.create(UserId.of(userId), eventId, rewardId);
        when(winnerService.createWinner(any(UserId.class), eq(eventId), eq(rewardId))).thenReturn(expectedWinner);

        // when
        WinnerDomain result = winnerApplicationService.selectWinner(eventId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEventId()).isEqualTo(eventId);
        assertThat(result.getRewardId()).isEqualTo(rewardId);
        assertThat(result.getStatus()).isEqualTo(RewardStatus.PENDING);
        
        verify(entryRepository).findUserIdByEventId(eventId);
        verify(winnerService).existsByEventId(eventId);
        verify(rewardRepository).findByEventId(eventId);
        verify(winnerService).createWinner(any(UserId.class), eq(eventId), eq(rewardId));
    }

    @Test
    @DisplayName("당첨자 선정 테스트 - 참가자 없음")
    void selectWinnerNoParticipantsTest() {
        // given
        Long eventId = 1L;
        when(entryRepository.findUserIdByEventId(eventId)).thenReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> winnerApplicationService.selectWinner(eventId))
            .isInstanceOf(RuntimeException.class);

        verify(entryRepository).findUserIdByEventId(eventId);
        verify(winnerService, never()).createWinner(any(), any(), any());
    }

    @Test
    @DisplayName("당첨자 선정 테스트 - 이미 당첨자 존재")
    void selectWinnerAlreadyExistsTest() {
        // given
        Long eventId = 1L;
        List<String> participants = Arrays.asList("user-1", "user-2", "user-3");
        
        when(entryRepository.findUserIdByEventId(eventId)).thenReturn(participants);
        when(winnerService.existsByEventId(eventId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> winnerApplicationService.selectWinner(eventId))
            .isInstanceOf(RuntimeException.class);

        verify(entryRepository).findUserIdByEventId(eventId);
        verify(winnerService).existsByEventId(eventId);
        verify(rewardRepository, never()).findByEventId(any());
        verify(winnerService, never()).createWinner(any(), any(), any());
    }

    @Test
    @DisplayName("당첨자 선정 테스트 - 보상 없음")
    void selectWinnerNoRewardTest() {
        // given
        Long eventId = 1L;
        List<String> participants = Arrays.asList("user-1", "user-2", "user-3");
        
        when(entryRepository.findUserIdByEventId(eventId)).thenReturn(participants);
        when(winnerService.existsByEventId(eventId)).thenReturn(false);
        when(rewardRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> winnerApplicationService.selectWinner(eventId))
            .isInstanceOf(RuntimeException.class);

        verify(entryRepository).findUserIdByEventId(eventId);
        verify(winnerService).existsByEventId(eventId);
        verify(rewardRepository).findByEventId(eventId);
        verify(winnerService, never()).createWinner(any(), any(), any());
    }

    @Test
    @DisplayName("보상 발송 테스트 - 정상 케이스 (최적화된 버전)")
    void sendRewardSuccessTest() {
        // given
        Long winnerId = 1L;
        String userEmail = "user@example.com";
        String eventName = "테스트 이벤트";
        String rewardUrl = "http://example.com/reward";
        
        // WinnerNotificationInfo 모킹 (최적화된 쿼리)
        WinnerNotificationInfo notificationInfo = new WinnerNotificationInfo(
                winnerId, userEmail, eventName, rewardUrl, RewardStatus.PENDING);
        
        when(winnerQueryRepository.findNotificationInfoById(winnerId))
                .thenReturn(Optional.of(notificationInfo));
        when(emailService.sendRewardEmail(userEmail, eventName, rewardUrl)).thenReturn(true);
        
        // WinnerService.updateStatus 호출 시 아무것도 하지 않도록 Mock 설정
        doNothing().when(winnerService).updateStatus(winnerId, RewardStatus.COMPLETE);

        // when
        boolean result = winnerApplicationService.sendReward(winnerId);

        // then
        assertThat(result).isTrue();
        verify(winnerQueryRepository).findNotificationInfoById(winnerId);
        verify(emailService).sendRewardEmail(userEmail, eventName, rewardUrl);
        verify(winnerService).updateStatus(winnerId, RewardStatus.COMPLETE);
        
        // 기존의 개별 repository 호출들은 더 이상 발생하지 않음
        verify(userRepository, never()).findById(any());
        verify(rewardRepository, never()).findById(any());
        verify(eventRepository, never()).findById(any());
    }

    @Test
    @DisplayName("보상 발송 테스트 - 당첨자 없음")
    void sendRewardWinnerNotFoundTest() {
        // given
        Long winnerId = 1L;
        when(winnerQueryRepository.findNotificationInfoById(winnerId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> winnerApplicationService.sendRewardManually(winnerId))
            .isInstanceOf(WinnerNotFoundException.class);

        verify(winnerQueryRepository).findNotificationInfoById(winnerId);
        verify(emailService, never()).sendRewardEmail(any(), any(), any());
        verify(winnerService, never()).updateStatus(any(Long.class), any());
    }

    @Test
    @DisplayName("보상 발송 테스트 - 이미 처리된 당첨자")
    void sendRewardAlreadyProcessedTest() {
        // given
        Long winnerId = 1L;
        WinnerNotificationInfo notificationInfo = new WinnerNotificationInfo(
                winnerId, "user@example.com", "이벤트", "http://reward.com", RewardStatus.COMPLETE);
        
        when(winnerQueryRepository.findNotificationInfoById(winnerId))
                .thenReturn(Optional.of(notificationInfo));

        // when & then
        assertThatThrownBy(() -> winnerApplicationService.sendRewardManually(winnerId))
            .isInstanceOf(WinnerAlreadyProcessedException.class);

        verify(winnerQueryRepository).findNotificationInfoById(winnerId);
        verify(emailService, never()).sendRewardEmail(any(), any(), any());
        verify(winnerService, never()).updateStatus(any(Long.class), any());
    }

    @Test
    @DisplayName("보상 발송 테스트 - 이메일 발송 실패 (최적화된 버전)")
    void sendRewardEmailFailureTest() {
        // given
        Long winnerId = 1L;
        String userEmail = "user@example.com";
        String eventName = "테스트 이벤트";
        String rewardUrl = "http://example.com/reward";
        
        WinnerNotificationInfo notificationInfo = new WinnerNotificationInfo(
                winnerId, userEmail, eventName, rewardUrl, RewardStatus.PENDING);
        
        when(winnerQueryRepository.findNotificationInfoById(winnerId))
                .thenReturn(Optional.of(notificationInfo));
        when(emailService.sendRewardEmail(userEmail, eventName, rewardUrl))
                .thenThrow(new RuntimeException("이메일 발송 실패"));
        
        // WinnerService.updateStatus 호출 시 아무것도 하지 않도록 Mock 설정
        doNothing().when(winnerService).updateStatus(winnerId, RewardStatus.FAILED);

        // when
        boolean result = winnerApplicationService.sendReward(winnerId);

        // then
        assertThat(result).isFalse();
        verify(winnerQueryRepository).findNotificationInfoById(winnerId);
        verify(emailService).sendRewardEmail(userEmail, eventName, rewardUrl);
        verify(winnerService).updateStatus(winnerId, RewardStatus.FAILED);
    }

    @Test
    @DisplayName("상태별 당첨자 조회 테스트")
    void getWinnersByStatusTest() {
        // given
        RewardStatus status = RewardStatus.PENDING;
        List<WinnerRewardDetailInfo> expectedWinners = Arrays.asList(
            mock(WinnerRewardDetailInfo.class),
            mock(WinnerRewardDetailInfo.class)
        );
        when(winnerQueryRepository.findDetailsByStatus(status)).thenReturn(expectedWinners);

        // when
        List<WinnerRewardDetailInfo> result = winnerApplicationService.getWinnersByStatus(status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedWinners);
        verify(winnerQueryRepository).findDetailsByStatus(status);
    }

    @Test
    @DisplayName("내 당첨 정보 조회 테스트")
    void getMyWinnersTest() {
        // given
        UserId userId = UserId.of("current-user");
        List<WinnerUserDetailInfo> expectedWinners = Arrays.asList(
            mock(WinnerUserDetailInfo.class),
            mock(WinnerUserDetailInfo.class)
        );
        
        when(winnerQueryRepository.findUserDetailsByUserId(userId.getValue())).thenReturn(expectedWinners);

        // when
        List<WinnerUserDetailInfo> result = winnerApplicationService.getMyWinners(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedWinners);
        verify(winnerQueryRepository).findUserDetailsByUserId(userId.getValue());
    }
}
