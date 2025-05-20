package com.seob.systeminfra.winner.service;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.repository.WinnerRepository;
import com.seob.systemdomain.winner.vo.RewardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WinnerServiceImplTest {

    @Mock
    private WinnerRepository winnerRepository;

    @InjectMocks
    private WinnerServiceImpl winnerService;

    @Test
    @DisplayName("Winner ID로 조회 테스트")
    void findByIdTest() {
        // given
        Long winnerId = 1L;
        WinnerDomain expectedWinner = WinnerDomain.of(
                winnerId, "user-1", 2L, 3L, null, RewardStatus.PENDING, null);
        
        when(winnerRepository.findById(winnerId)).thenReturn(Optional.of(expectedWinner));

        // when
        Optional<WinnerDomain> result = winnerService.findById(winnerId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedWinner);
        verify(winnerRepository).findById(winnerId);
    }

    @Test
    @DisplayName("상태별 Winner 조회 테스트")
    void findByStatusTest() {
        // given
        RewardStatus status = RewardStatus.PENDING;
        WinnerDomain winner1 = WinnerDomain.of(1L, "user-1", 2L, 3L, null, status, null);
        WinnerDomain winner2 = WinnerDomain.of(2L, "user-2", 3L, 4L, null, status, null);
        List<WinnerDomain> expectedWinners = Arrays.asList(winner1, winner2);
        
        when(winnerRepository.findByStatus(status)).thenReturn(expectedWinners);

        // when
        List<WinnerDomain> result = winnerService.findByStatus(status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedWinners);
        verify(winnerRepository).findByStatus(status);
    }

    @Test
    @DisplayName("이벤트 ID로 Winner 존재 여부 확인 테스트")
    void existsByEventIdTest() {
        // given
        Long eventId = 1L;
        when(winnerRepository.existsByEventId(eventId)).thenReturn(true);

        // when
        boolean result = winnerService.existsByEventId(eventId);

        // then
        assertThat(result).isTrue();
        verify(winnerRepository).existsByEventId(eventId);
    }

    @Test
    @DisplayName("Winner 생성 테스트")
    void createWinnerTest() {
        // given
        UserId userId = UserId.of("user-1");
        Long eventId = 1L;
        Long rewardId = 2L;
        
        WinnerDomain expectedWinner = WinnerDomain.create(userId, eventId, rewardId);
        when(winnerRepository.save(any(WinnerDomain.class))).thenReturn(expectedWinner);

        // when
        WinnerDomain result = winnerService.createWinner(userId, eventId, rewardId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getEventId()).isEqualTo(eventId);
        assertThat(result.getRewardId()).isEqualTo(rewardId);
        assertThat(result.getStatus()).isEqualTo(RewardStatus.PENDING);
        verify(winnerRepository).save(any(WinnerDomain.class));
    }

    @Test
    @DisplayName("Winner 상태 업데이트 테스트 - COMPLETE 상태로")
    void updateStatusToCompleteTest() {
        // given
        WinnerDomain winner = WinnerDomain.create(UserId.of("user-1"), 1L, 2L);
        RewardStatus newStatus = RewardStatus.COMPLETE;
        
        // when
        winnerService.updateStatus(winner, newStatus);

        // then
        assertThat(winner.getStatus()).isEqualTo(RewardStatus.COMPLETE);
        assertThat(winner.getSentAt()).isNotNull();
        verify(winnerRepository).save(winner);
    }

    @Test
    @DisplayName("Winner 상태 업데이트 테스트 - FAILED 상태로")
    void updateStatusToFailedTest() {
        // given
        WinnerDomain winner = WinnerDomain.create(UserId.of("user-1"), 1L, 2L);
        RewardStatus newStatus = RewardStatus.FAILED;
        
        // when
        winnerService.updateStatus(winner, newStatus);

        // then
        assertThat(winner.getStatus()).isEqualTo(RewardStatus.FAILED);
        verify(winnerRepository).save(winner);
    }
}
