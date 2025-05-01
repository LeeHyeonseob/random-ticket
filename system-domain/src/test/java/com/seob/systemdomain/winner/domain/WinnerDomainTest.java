package com.seob.systemdomain.winner.domain;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.vo.RewardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WinnerDomainTest {

    @Test
    @DisplayName("당첨자 생성")
    void createWinner() {
        // given
        String userId = "user123";
        Long eventId = 1L;
        Long rewardId = 456L;
        
        // when
        WinnerDomain winnerDomain = WinnerDomain.create(UserId.of(userId), eventId, rewardId);
        
        // then
        assertThat(winnerDomain).isNotNull();
        assertThat(winnerDomain.getId()).isNull(); // DB 저장 전이므로 ID는 null
        assertThat(winnerDomain.getUserId().getValue()).isEqualTo(userId);
        assertThat(winnerDomain.getEventId()).isEqualTo(eventId);
        assertThat(winnerDomain.getRewardId()).isEqualTo(rewardId);
        assertThat(winnerDomain.getStatus()).isEqualTo(RewardStatus.PENDING);
        assertThat(winnerDomain.getSentAt()).isNull(); // 아직 발송되지 않음
    }
    
    @Test
    @DisplayName("당첨 상태 변경 - 발송 완료")
    void changeStatusToComplete() {
        // given
        String userId = "user123";
        Long eventId = 1L;
        Long rewardId = 456L;
        
        WinnerDomain winnerDomain = WinnerDomain.create(UserId.of(userId), eventId, rewardId);
        assertThat(winnerDomain.getStatus()).isEqualTo(RewardStatus.PENDING);
        assertThat(winnerDomain.getSentAt()).isNull();
        
        // when
        winnerDomain.send();
        
        // then
        assertThat(winnerDomain.getStatus()).isEqualTo(RewardStatus.COMPLETE);
        assertThat(winnerDomain.getSentAt()).isNotNull(); // 발송 시간이 기록됨
    }
    
    @Test
    @DisplayName("당첨 상태 변경 - 실패")
    void changeStatusToFailed() {
        // given
        String userId = "user123";
        Long eventId = 1L;
        Long rewardId = 456L;
        
        WinnerDomain winnerDomain = WinnerDomain.create(UserId.of(userId), eventId, rewardId);
        assertThat(winnerDomain.getStatus()).isEqualTo(RewardStatus.PENDING);
        
        // when
        winnerDomain.markAsFailed();
        
        // then
        assertThat(winnerDomain.getStatus()).isEqualTo(RewardStatus.FAILED);
    }
    
    @Test
    @DisplayName("기존 당첨 정보 로드")
    void loadExistingWinner() {
        // given
        Long id = 1L;
        String userId = "user123";
        Long eventId = 1L;
        Long rewardId = 456L;
        RewardStatus status = RewardStatus.COMPLETE;
        LocalDateTime sentAt = LocalDateTime.now();
        
        // when
        WinnerDomain winnerDomain = WinnerDomain.of(id, userId, eventId, rewardId, status, sentAt);
        
        // then
        assertThat(winnerDomain.getId()).isEqualTo(id);
        assertThat(winnerDomain.getUserId().getValue()).isEqualTo(userId);
        assertThat(winnerDomain.getEventId()).isEqualTo(eventId);
        assertThat(winnerDomain.getRewardId()).isEqualTo(rewardId);
        assertThat(winnerDomain.getStatus()).isEqualTo(status);
        assertThat(winnerDomain.getSentAt()).isEqualTo(sentAt);
    }
    
    @Test
    @DisplayName("당첨자 정보 비교")
    void compareWinners() {
        // given
        String userId = "user123";
        Long eventId = 1L;
        Long rewardId = 456L;
        
        // when
        WinnerDomain winner1 = WinnerDomain.create(UserId.of(userId), eventId, rewardId);
        WinnerDomain winner2 = WinnerDomain.create(UserId.of(userId), eventId, rewardId);
        
        // then
        // ID는 null로 초기화됨(DB 저장 시 할당)
        assertNull(winner1.getId());
        assertNull(winner2.getId());
        
        // 동일한 사용자, 이벤트, 리워드이지만 다른 당첨자 인스턴스
        assertEquals(winner1.getUserId(), winner2.getUserId());
        assertEquals(winner1.getEventId(), winner2.getEventId());
        assertEquals(winner1.getRewardId(), winner2.getRewardId());
        assertEquals(winner1.getStatus(), winner2.getStatus());
    }
}