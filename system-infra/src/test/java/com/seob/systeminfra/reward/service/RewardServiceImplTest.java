package com.seob.systeminfra.reward.service;

import com.seob.systemcore.error.exception.RewardException;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.repository.RewardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Test
    @DisplayName("보상 생성 성공")
    void createReward_Success() {
        // given
        Long eventId = 1L;
        String rewardName = "Gift Card";
        String resourceUrl = "http://example.com";
        
        RewardDomain expectedReward = RewardDomain.create(eventId, rewardName, resourceUrl);
        
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(rewardRepository.existsByEventId(eventId)).thenReturn(false);
        when(rewardRepository.save(any(RewardDomain.class))).thenReturn(expectedReward);

        // when
        RewardDomain result = rewardService.createReward(eventId, rewardName, resourceUrl);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEventId()).isEqualTo(eventId);
        assertThat(result.getName()).isEqualTo(rewardName);
        assertThat(result.getResourceUrl()).isEqualTo(resourceUrl);
        
        verify(eventRepository).existsById(eventId);
        verify(rewardRepository).existsByEventId(eventId);
        verify(rewardRepository).save(any(RewardDomain.class));
    }

    @Test
    @DisplayName("보상 생성 실패 - null eventId")
    void createReward_FailWithNullEventId() {
        // given
        Long eventId = null;
        String rewardName = "Gift Card";
        String resourceUrl = "http://example.com";

        // when & then
        assertThatThrownBy(() -> rewardService.createReward(eventId, rewardName, resourceUrl))
                .isInstanceOf(RewardException.class);
    }

    @Test
    @DisplayName("보상 생성 실패 - 존재하지 않는 이벤트")
    void createReward_FailWithNonExistentEvent() {
        // given
        Long eventId = 1L;
        String rewardName = "Gift Card";
        String resourceUrl = "http://example.com";
        
        when(eventRepository.existsById(eventId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> rewardService.createReward(eventId, rewardName, resourceUrl))
                .isInstanceOf(RewardException.class);
    }

    @Test
    @DisplayName("보상 생성 실패 - 이미 보상이 존재")
    void createReward_FailWithExistingReward() {
        // given
        Long eventId = 1L;
        String rewardName = "Gift Card";
        String resourceUrl = "http://example.com";
        
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(rewardRepository.existsByEventId(eventId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> rewardService.createReward(eventId, rewardName, resourceUrl))
                .isInstanceOf(RewardException.class);
    }

    @Test
    @DisplayName("이벤트 ID로 보상 조회 성공")
    void getRewardByEventId_Success() {
        // given
        Long eventId = 1L;
        RewardDomain expectedReward = RewardDomain.of(1L, eventId, "Gift Card", "http://example.com", LocalDateTime.now());
        
        when(rewardRepository.findByEventId(eventId)).thenReturn(Optional.of(expectedReward));

        // when
        RewardDomain result = rewardService.getRewardByEventId(eventId);

        // then
        assertThat(result).isEqualTo(expectedReward);
        verify(rewardRepository).findByEventId(eventId);
    }

    @Test
    @DisplayName("이벤트 ID로 보상 조회 실패 - 보상 없음")
    void getRewardByEventId_FailWithNoReward() {
        // given
        Long eventId = 1L;
        when(rewardRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rewardService.getRewardByEventId(eventId))
                .isInstanceOf(RewardException.class);
    }

    @Test
    @DisplayName("모든 보상 조회")
    void getAllRewards() {
        // given
        List<RewardDomain> expectedRewards = List.of(
                RewardDomain.of(1L, 1L, "Gift Card 1", "http://example1.com", LocalDateTime.now()),
                RewardDomain.of(2L, 2L, "Gift Card 2", "http://example2.com", LocalDateTime.now())
        );
        
        when(rewardRepository.findAll()).thenReturn(expectedRewards);

        // when
        List<RewardDomain> result = rewardService.getAllRewards();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedRewards);
        verify(rewardRepository).findAll();
    }

    @Test
    @DisplayName("보상 수정 성공")
    void updateReward_Success() {
        // given
        Long rewardId = 1L;
        String newRewardName = "Updated Gift Card";
        String newResourceUrl = "http://updated.com";
        
        RewardDomain existingReward = RewardDomain.of(rewardId, 1L, "Old Gift Card", "http://old.com", LocalDateTime.now());
        RewardDomain updatedReward = existingReward.update(newRewardName, newResourceUrl);
        
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.of(existingReward));
        when(rewardRepository.save(any(RewardDomain.class))).thenReturn(updatedReward);

        // when
        RewardDomain result = rewardService.updateReward(rewardId, newRewardName, newResourceUrl);

        // then
        assertThat(result.getName()).isEqualTo(newRewardName);
        assertThat(result.getResourceUrl()).isEqualTo(newResourceUrl);
        verify(rewardRepository).findById(rewardId);
        verify(rewardRepository).save(any(RewardDomain.class));
    }

    @Test
    @DisplayName("보상 수정 실패 - 보상 없음")
    void updateReward_FailWithNoReward() {
        // given
        Long rewardId = 1L;
        String newRewardName = "Updated Gift Card";
        String newResourceUrl = "http://updated.com";
        
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rewardService.updateReward(rewardId, newRewardName, newResourceUrl))
                .isInstanceOf(RewardException.class);
    }

    @Test
    @DisplayName("보상 삭제 성공")
    void deleteReward_Success() {
        // given
        Long rewardId = 1L;
        RewardDomain existingReward = RewardDomain.of(rewardId, 1L, "Gift Card", "http://example.com", LocalDateTime.now());
        
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.of(existingReward));

        // when
        rewardService.deleteReward(rewardId);

        // then
        verify(rewardRepository).findById(rewardId);
        verify(rewardRepository).deleteById(rewardId);
    }

    @Test
    @DisplayName("보상 삭제 실패 - 보상 없음")
    void deleteReward_FailWithNoReward() {
        // given
        Long rewardId = 1L;
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rewardService.deleteReward(rewardId))
                .isInstanceOf(RewardException.class);
    }
}
