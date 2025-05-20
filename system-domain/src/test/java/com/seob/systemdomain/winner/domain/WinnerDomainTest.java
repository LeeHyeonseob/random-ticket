package com.seob.systemdomain.winner.domain;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.vo.RewardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WinnerDomainTest {

    @Test
    @DisplayName("Winner 도메인 객체 생성 테스트")
    void createWinnerTest() {
        // given
        UserId userId = UserId.of("test-user-id");
        Long eventId = 1L;
        Long rewardId = 2L;
        Long entryId = 3L;

        // when
        WinnerDomain winner = WinnerDomain.create(userId, eventId, rewardId, entryId);

        // then
        assertThat(winner).isNotNull();
        assertThat(winner.getUserId()).isEqualTo(userId);
        assertThat(winner.getEventId()).isEqualTo(eventId);
        assertThat(winner.getRewardId()).isEqualTo(rewardId);
        assertThat(winner.getEntryId()).isEqualTo(entryId);
        assertThat(winner.getStatus()).isEqualTo(RewardStatus.PENDING);
        assertThat(winner.getSentAt()).isNull();
    }

    @Test
    @DisplayName("Winner 도메인 객체 생성 테스트 - entryId 없이")
    void createWinnerWithoutEntryIdTest() {
        // given
        UserId userId = UserId.of("test-user-id");
        Long eventId = 1L;
        Long rewardId = 2L;

        // when
        WinnerDomain winner = WinnerDomain.create(userId, eventId, rewardId);

        // then
        assertThat(winner).isNotNull();
        assertThat(winner.getUserId()).isEqualTo(userId);
        assertThat(winner.getEventId()).isEqualTo(eventId);
        assertThat(winner.getRewardId()).isEqualTo(rewardId);
        assertThat(winner.getEntryId()).isNull();
        assertThat(winner.getStatus()).isEqualTo(RewardStatus.PENDING);
    }

    @Test
    @DisplayName("Winner 도메인 객체 정적 팩토리 메서드 테스트")
    void winnerOfTest() {
        // given
        Long id = 1L;
        String userId = "test-user-id";
        Long eventId = 2L;
        Long rewardId = 3L;
        Long entryId = 4L;
        RewardStatus status = RewardStatus.PENDING;
        LocalDateTime sentAt = LocalDateTime.now();

        // when
        WinnerDomain winner = WinnerDomain.of(id, userId, eventId, rewardId, entryId, status, sentAt);

        // then
        assertThat(winner).isNotNull();
        assertThat(winner.getId()).isEqualTo(id);
        assertThat(winner.getUserId().getValue()).isEqualTo(userId);
        assertThat(winner.getEventId()).isEqualTo(eventId);
        assertThat(winner.getRewardId()).isEqualTo(rewardId);
        assertThat(winner.getEntryId()).isEqualTo(entryId);
        assertThat(winner.getStatus()).isEqualTo(status);
        assertThat(winner.getSentAt()).isEqualTo(sentAt);
    }

    @Test
    @DisplayName("Winner 도메인 객체 정적 팩토리 메서드 테스트 - entryId 없이")
    void winnerOfWithoutEntryIdTest() {
        // given
        Long id = 1L;
        String userId = "test-user-id";
        Long eventId = 2L;
        Long rewardId = 3L;
        RewardStatus status = RewardStatus.PENDING;
        LocalDateTime sentAt = LocalDateTime.now();

        // when
        WinnerDomain winner = WinnerDomain.of(id, userId, eventId, rewardId, status, sentAt);

        // then
        assertThat(winner).isNotNull();
        assertThat(winner.getId()).isEqualTo(id);
        assertThat(winner.getUserId().getValue()).isEqualTo(userId);
        assertThat(winner.getEventId()).isEqualTo(eventId);
        assertThat(winner.getRewardId()).isEqualTo(rewardId);
        assertThat(winner.getEntryId()).isNull();
        assertThat(winner.getStatus()).isEqualTo(status);
        assertThat(winner.getSentAt()).isEqualTo(sentAt);
    }

    @Test
    @DisplayName("보상 전송 완료 처리 테스트")
    void sendTest() {
        // given
        WinnerDomain winner = WinnerDomain.create(
                UserId.of("test-user-id"), 1L, 2L);
        assertThat(winner.getSentAt()).isNull();
        assertThat(winner.getStatus()).isEqualTo(RewardStatus.PENDING);

        // when
        winner.send();

        // then
        assertThat(winner.getStatus()).isEqualTo(RewardStatus.COMPLETE);
        assertThat(winner.getSentAt()).isNotNull();
    }

    @Test
    @DisplayName("보상 전송 실패 처리 테스트")
    void markAsFailedTest() {
        // given
        WinnerDomain winner = WinnerDomain.create(
                UserId.of("test-user-id"), 1L, 2L);
        assertThat(winner.getStatus()).isEqualTo(RewardStatus.PENDING);

        // when
        winner.markAsFailed();

        // then
        assertThat(winner.getStatus()).isEqualTo(RewardStatus.FAILED);
    }
    
    @Test
    @DisplayName("당첨자 정보 비교 테스트")
    void compareWinnersTest() {
        // given
        UserId userId = UserId.of("test-user-id");
        Long eventId = 1L;
        Long rewardId = 2L;
        
        // when
        WinnerDomain winner1 = WinnerDomain.create(userId, eventId, rewardId);
        WinnerDomain winner2 = WinnerDomain.create(userId, eventId, rewardId);
        
        // then
        // ID는 null로 초기화됨(DB 저장 시 할당)
        assertThat(winner1.getId()).isNull();
        assertThat(winner2.getId()).isNull();
        
        // 동일한 사용자, 이벤트, 리워드이지만 다른 당첨자 인스턴스
        assertThat(winner1.getUserId()).isEqualTo(winner2.getUserId());
        assertThat(winner1.getEventId()).isEqualTo(winner2.getEventId());
        assertThat(winner1.getRewardId()).isEqualTo(winner2.getRewardId());
        assertThat(winner1.getStatus()).isEqualTo(winner2.getStatus());
    }
}
