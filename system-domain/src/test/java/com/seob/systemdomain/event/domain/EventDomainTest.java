package com.seob.systemdomain.event.domain;

import com.seob.systemdomain.event.vo.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventDomainTest {

    @Test
    @DisplayName("이벤트 생성")
    void createEventDomain() {
        //given
        String name = "오늘_날짜_이벤트";
        String description = "기프티콘 뿌릴거다";
        LocalDate eventDate = LocalDate.of(2025, 1, 22);

        //when
        EventDomain eventDomain = EventDomain.create(name, description,eventDate);

        //then
        assertThat(eventDomain).isNotNull();
        assertThat(eventDomain.getName()).isEqualTo(name);
        assertThat(eventDomain.getDescription()).isEqualTo(description);
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.SCHEDULED);
        assertThat(eventDomain.getCreatedAt()).isNotNull();
        assertThat(eventDomain.getEventDate()).isEqualTo(eventDate);
    }

    @Test
    @DisplayName("이벤트 응모 가능 검사")
    void check_can_apply(){
        //given
        String name = "오늘_날짜_이벤트";
        String description = "기프티콘 뿌릴거다";
        LocalDate eventDate = LocalDate.of(2025, 1, 22);


        EventDomain eventDomain = EventDomain.create(name, description, eventDate);

        //when
        eventDomain.openEvent();

        //then
        assertThat(eventDomain.canApply()).isTrue();
    }

    // ---------------------- 여기서부터 추가된 테스트 코드 ----------------------
    
    @Test
    @DisplayName("이벤트 종료")
    void closeEvent() {
        // given
        String name = "테스트_이벤트";
        String description = "이벤트 설명";
        LocalDate eventDate = LocalDate.of(2025, 1, 22);
        
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);
        eventDomain.openEvent();
        
        // 이벤트가 OPEN 상태인지 확인
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.OPEN);
        
        // when
        eventDomain.closeEvent();
        
        // then
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.CLOSED);
        assertThat(eventDomain.canApply()).isFalse();
    }
    
    @Test
    @DisplayName("상태에 따른 이벤트 참가 가능 여부 확인")
    void canApply_forDifferentStatuses() {
        // given
        String name = "테스트_이벤트";
        String description = "이벤트 설명";
        LocalDate eventDate = LocalDate.of(2025, 1, 22);
        
        // SCHEDULED 상태 이벤트
        EventDomain scheduledEvent = EventDomain.create(name, description, eventDate);
        assertThat(scheduledEvent.getStatus()).isEqualTo(EventStatus.SCHEDULED);
        assertThat(scheduledEvent.canApply()).isFalse();
        
        // OPEN 상태 이벤트
        EventDomain openEvent = EventDomain.create(name, description, eventDate);
        openEvent.openEvent();
        assertThat(openEvent.getStatus()).isEqualTo(EventStatus.OPEN);
        assertThat(openEvent.canApply()).isTrue();
        
        // CLOSED 상태 이벤트
        EventDomain closedEvent = EventDomain.create(name, description, eventDate);
        closedEvent.openEvent();
        closedEvent.closeEvent();
        assertThat(closedEvent.getStatus()).isEqualTo(EventStatus.CLOSED);
        assertThat(closedEvent.canApply()).isFalse();
    }
    
    @Test
    @DisplayName("상태 변경 메서드 테스트")
    void changeStatusTest() {
        // given
        String name = "테스트_이벤트";
        String description = "이벤트 설명";
        LocalDate eventDate = LocalDate.of(2025, 1, 22);
        
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.SCHEDULED);
        
        // when - OPEN으로 변경
        eventDomain.changeStatus(EventStatus.OPEN);
        
        // then
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.OPEN);
        
        // when - CLOSED로 변경
        eventDomain.changeStatus(EventStatus.CLOSED);
        
        // then
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.CLOSED);
    }
    
    @Test
    @DisplayName("이벤트 종료 처리 필요 여부 확인")
    void shouldBeClosedTest() {
        // given
        String name = "테스트_이벤트";
        String description = "이벤트 설명";
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        // 어제 날짜 & OPEN 상태 -> 종료 필요
        EventDomain pastOpenEvent = EventDomain.create(name, description, yesterday);
        pastOpenEvent.openEvent();
        
        // 어제 날짜 & CLOSED 상태 -> 이미 종료됨
        EventDomain pastClosedEvent = EventDomain.create(name, description, yesterday);
        pastClosedEvent.closeEvent();
        
        // 내일 날짜 & OPEN 상태 -> 종료 불필요
        EventDomain futureEvent = EventDomain.create(name, description, tomorrow);
        futureEvent.openEvent();
        
        // when & then
        assertThat(pastOpenEvent.shouldBeClosed(LocalDate.now())).isTrue();
        assertThat(pastClosedEvent.shouldBeClosed(LocalDate.now())).isFalse();
        assertThat(futureEvent.shouldBeClosed(LocalDate.now())).isFalse();
    }
    
    @Test
    @DisplayName("of 메서드로 기존 이벤트 로드")
    void loadExistingEvent() {
        // given
        Long id = 1L;
        String name = "테스트_이벤트";
        String description = "이벤트 설명";
        EventStatus status = EventStatus.OPEN;
        LocalDate eventDate = LocalDate.of(2025, 1, 22);
        LocalDateTime createdAt = LocalDateTime.now();
        
        // when
        EventDomain eventDomain = EventDomain.of(id, name, description, status, eventDate, createdAt);
        
        // then
        assertThat(eventDomain.getId()).isEqualTo(id);
        assertThat(eventDomain.getName()).isEqualTo(name);
        assertThat(eventDomain.getDescription()).isEqualTo(description);
        assertThat(eventDomain.getStatus()).isEqualTo(status);
        assertThat(eventDomain.getEventDate()).isEqualTo(eventDate);
        assertThat(eventDomain.getCreatedAt()).isEqualTo(createdAt);
    }
}