package com.seob.systemdomain.reward.domain;

import com.seob.systemcore.error.exception.RewardException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RewardDomainTest {

    @Test
    @DisplayName("리워드 생성 성공")
    void createReward() {
        // given
        Long eventId = 1L;
        String name = "reward1";
        String resourceUrl = "http://example.com/gift-card/1234";
        
        // when
        RewardDomain rewardDomain = RewardDomain.create(eventId, name, resourceUrl);
        
        // then
        assertThat(rewardDomain).isNotNull();
        assertThat(rewardDomain.getId()).isNull(); // DB 저장 전이므로 ID는 null
        assertThat(rewardDomain.getEventId()).isEqualTo(eventId);
        assertThat(rewardDomain.getName()).isEqualTo(name);
        assertThat(rewardDomain.getResourceUrl()).isEqualTo(resourceUrl);
        assertThat(rewardDomain.getCreatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("기존 리워드 데이터 복원")
    void loadExistingReward() {
        // given
        Long id = 1L;
        Long eventId = 1L;
        String name = "reward1";
        String resourceUrl = "http://example.com/gift-card/1234";
        LocalDateTime createdAt = LocalDateTime.now();
        
        // when
        RewardDomain rewardDomain = RewardDomain.of(id, eventId, name, resourceUrl, createdAt);
        
        // then
        assertThat(rewardDomain.getId()).isEqualTo(id);
        assertThat(rewardDomain.getEventId()).isEqualTo(eventId);
        assertThat(rewardDomain.getName()).isEqualTo(name);
        assertThat(rewardDomain.getResourceUrl()).isEqualTo(resourceUrl);
        assertThat(rewardDomain.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("리워드 정보 업데이트")
    void updateRewardInfo() {
        // given
        RewardDomain originalReward = RewardDomain.of(
                1L, 1L, "original", "http://original.com", LocalDateTime.now()
        );
        String newName = "updated";
        String newUrl = "http://updated.com";
        
        // when
        RewardDomain updatedReward = originalReward.update(newName, newUrl);
        
        // then
        assertThat(updatedReward.getId()).isEqualTo(originalReward.getId());
        assertThat(updatedReward.getEventId()).isEqualTo(originalReward.getEventId());
        assertThat(updatedReward.getName()).isEqualTo(newName);
        assertThat(updatedReward.getResourceUrl()).isEqualTo(newUrl);
        assertThat(updatedReward.getCreatedAt()).isEqualTo(originalReward.getCreatedAt());
    }

    @Test
    @DisplayName("빈 이름으로 리워드 생성 실패")
    void createRewardWithEmptyName() {
        // given
        Long eventId = 1L;
        String emptyName = "";
        String resourceUrl = "http://example.com";
        
        // when & then
        assertThatThrownBy(() -> RewardDomain.create(eventId, emptyName, resourceUrl))
                .isInstanceOf(RewardException.class);
    }

    @Test
    @DisplayName("긴 이름으로 리워드 생성 실패")
    void createRewardWithLongName() {
        // given
        Long eventId = 1L;
        String longName = "a".repeat(101); // 101자
        String resourceUrl = "http://example.com";
        
        // when & then
        assertThatThrownBy(() -> RewardDomain.create(eventId, longName, resourceUrl))
                .isInstanceOf(RewardException.class);
    }

    @Test
    @DisplayName("잘못된 URL로 리워드 생성 실패")
    void createRewardWithInvalidUrl() {
        // given
        Long eventId = 1L;
        String name = "reward";
        String invalidUrl = "invalid-url";
        
        // when & then
        assertThatThrownBy(() -> RewardDomain.create(eventId, name, invalidUrl))
                .isInstanceOf(RewardException.class);
    }
    
    @Test
    @DisplayName("리워드 생성 시간 검증")
    void validateCreationTime() {
        // given
        Long eventId = 1L;
        String name = "reward1";
        String resourceUrl = "http://example.com/gift-card/1234";
        
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        
        // when
        RewardDomain rewardDomain = RewardDomain.create(eventId, name, resourceUrl);
        
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        
        // then
        assertThat(rewardDomain.getCreatedAt()).isAfterOrEqualTo(beforeCreation);
        assertThat(rewardDomain.getCreatedAt()).isBeforeOrEqualTo(afterCreation);
    }

    @Test
    @DisplayName("공백이 포함된 이름과 URL 처리")
    void handleWhitespaceInNameAndUrl() {
        // given
        Long eventId = 1L;
        String nameWithSpaces = "  reward name  ";
        String urlWithSpaces = "http://example.com  ";
        
        // when
        RewardDomain rewardDomain = RewardDomain.create(eventId, nameWithSpaces, urlWithSpaces);
        
        // then
        assertThat(rewardDomain.getName()).isEqualTo("reward name");
        assertThat(rewardDomain.getResourceUrl()).isEqualTo("http://example.com");
    }
}
