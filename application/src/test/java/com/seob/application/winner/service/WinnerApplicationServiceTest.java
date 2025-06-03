package com.seob.application.winner.service;

import com.seob.application.winner.exception.AlreadyWinnerExistsException;
import com.seob.application.winner.exception.EntryNotFoundException;
import com.seob.application.winner.exception.NoRewardInEventException;
import com.seob.application.winner.exception.WinnerNotFoundException;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.repository.RewardRepository;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.Email;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
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
        
        // EventRepository mock 설정
        when(entryRepository.findUserIdByEventId(eventId)).thenReturn(participants);
        when(winnerService.existsByEventId(eventId)).thenReturn(false);
        
        // RewardRepository mock 설정
        RewardDomain rewardDomain = RewardDomain.of(rewardId, eventId, "테스트 보상", "http://example.com/reward", LocalDateTime.now());
        when(rewardRepository.findByEventId(eventId)).thenReturn(Optional.of(rewardDomain));
        
        // WinnerService mock 설정
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
            .isInstanceOf(EntryNotFoundException.class);
        
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
            .isInstanceOf(AlreadyWinnerExistsException.class);
        
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
            .isInstanceOf(NoRewardInEventException.class);
        
        verify(entryRepository).findUserIdByEventId(eventId);
        verify(winnerService).existsByEventId(eventId);
        verify(rewardRepository).findByEventId(eventId);
        verify(winnerService, never()).createWinner(any(), any(), any());
    }

    @Test
    @DisplayName("보상 발송 테스트 - 정상 케이스")
    void sendRewardSuccessTest() {
        // given
        Long winnerId = 1L;
        Long eventId = 2L;
        Long rewardId = 3L;
        String userId = "user-1";
        String userEmail = "user@example.com";
        String rewardUrl = "http://example.com/reward";
        String eventName = "테스트 이벤트";
        
        WinnerDomain winnerDomain = WinnerDomain.of(winnerId, userId, eventId, rewardId, null, RewardStatus.PENDING, null);
        
        // 사용자 도메인과 이메일 객체 설정
        UserDomain userDomain = mock(UserDomain.class);
        Email email = Email.from(userEmail);
        when(userDomain.getEmail()).thenReturn(email);
        
        // 보상 도메인 설정
        RewardDomain rewardDomain = RewardDomain.of(rewardId, eventId, "테스트 보상", rewardUrl, LocalDateTime.now());
        
        // 이벤트 도메인 설정
        EventDomain eventDomain = mock(EventDomain.class);
        when(eventDomain.getName()).thenReturn(eventName);
        
        // 레포지토리 모킹
        when(winnerService.findById(winnerId)).thenReturn(Optional.of(winnerDomain));
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(userDomain));
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.of(rewardDomain));
        when(eventRepository.findById(eventId)).thenReturn(eventDomain);
        
        // 이메일 서비스 모킹
        when(emailService.sendRewardEmail(userEmail, eventName, rewardUrl)).thenReturn(true);

        // when
        boolean result = winnerApplicationService.sendReward(winnerId);

        // then
        assertThat(result).isTrue();
        verify(winnerService).findById(winnerId);
        verify(userRepository).findById(any(UserId.class));
        verify(rewardRepository).findById(rewardId);
        verify(eventRepository).findById(eventId);
        verify(emailService).sendRewardEmail(userEmail, eventName, rewardUrl);
        verify(winnerService).updateStatus(winnerDomain, RewardStatus.COMPLETE);
    }

    @Test
    @DisplayName("보상 발송 테스트 - 당첨자 없음")
    void sendRewardWinnerNotFoundTest() {
        // given
        Long winnerId = 1L;
        when(winnerService.findById(winnerId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> winnerApplicationService.sendRewardManually(winnerId))
            .isInstanceOf(WinnerNotFoundException.class);
        
        verify(winnerService).findById(winnerId);
        verify(emailService, never()).sendRewardEmail(any(), any(), any());
        verify(winnerService, never()).updateStatus(any(), any());
    }

    @Test
    @DisplayName("보상 발송 테스트 - 이메일 발송 실패")
    void sendRewardEmailFailureTest() {
        // given
        Long winnerId = 1L;
        Long eventId = 2L;
        Long rewardId = 3L;
        String userId = "user-1";
        String userEmail = "user@example.com";
        String rewardUrl = "http://example.com/reward";
        String eventName = "테스트 이벤트";
        
        WinnerDomain winnerDomain = WinnerDomain.of(winnerId, userId, eventId, rewardId, null, RewardStatus.PENDING, null);
        
        // 사용자 도메인과 이메일 객체 설정
        UserDomain userDomain = mock(UserDomain.class);
        Email email = Email.from(userEmail);
        when(userDomain.getEmail()).thenReturn(email);
        
        // 보상 도메인 설정
        RewardDomain rewardDomain = RewardDomain.of(rewardId, eventId, "테스트 보상", rewardUrl, LocalDateTime.now());
        
        // 이벤트 도메인 설정
        EventDomain eventDomain = mock(EventDomain.class);
        when(eventDomain.getName()).thenReturn(eventName);
        
        // 레포지토리 모킹
        when(winnerService.findById(winnerId)).thenReturn(Optional.of(winnerDomain));
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(userDomain));
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.of(rewardDomain));
        when(eventRepository.findById(eventId)).thenReturn(eventDomain);
        
        // 이메일 서비스 예외 발생 모킹
        when(emailService.sendRewardEmail(userEmail, eventName, rewardUrl))
            .thenThrow(new RuntimeException("이메일 발송 실패"));

        // when
        boolean result = winnerApplicationService.sendReward(winnerId);

        // then
        assertThat(result).isFalse();
        verify(winnerService).findById(winnerId);
        verify(userRepository).findById(any(UserId.class));
        verify(rewardRepository).findById(rewardId);
        verify(eventRepository).findById(eventId);
        verify(emailService).sendRewardEmail(userEmail, eventName, rewardUrl);
        verify(winnerService).updateStatus(winnerDomain, RewardStatus.FAILED);
    }

    @Test
    @DisplayName("상태별 당첨자 조회 테스트")
    void getWinnersByStatusTest() {
        // given
        RewardStatus status = RewardStatus.PENDING;
        List<WinnerRewardDetailInfo> expectedWinners = Arrays.asList(
            WinnerRewardDetailInfo.of(1L, "user-1", "User 1", "user1@example.com", 
                2L, "Event 1", "Description 1", 3L, "Reward 1", status, null),
            WinnerRewardDetailInfo.of(2L, "user-2", "User 2", "user2@example.com", 
                3L, "Event 2", "Description 2", 4L, "Reward 2", status, null)
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
