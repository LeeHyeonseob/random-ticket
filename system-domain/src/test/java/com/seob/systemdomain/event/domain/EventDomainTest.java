package com.seob.systemdomain.event.domain;

import com.seob.systemdomain.event.exception.EventNotOpenedException;
import com.seob.systemdomain.event.exception.InvalidEventStatusException;
import com.seob.systemdomain.event.vo.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class EventDomainTest {

    private String name;
    private String description;
    private LocalDate eventDate;

    @BeforeEach
    void setUp() {
        name = "테스트_이벤트";
        description = "이벤트 설명";
        eventDate = LocalDate.of(2025, 1, 22);
    }

    @Test
    @DisplayName("이벤트 생성")
    void createEventDomain() {
        //when
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);

        //then
        assertThat(eventDomain).isNotNull();
        assertThat(eventDomain.getName()).isEqualTo(name);
        assertThat(eventDomain.getDescription()).isEqualTo(description);
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.SCHEDULED);
        assertThat(eventDomain.getCreatedAt()).isNotNull();
        assertThat(eventDomain.getEventDate()).isEqualTo(eventDate);
    }

    @Test
    @DisplayName("기존 이벤트 로드 - of 메서드")
    void loadExistingEvent() {
        //given
        Long id = 1L;
        EventStatus status = EventStatus.OPEN;
        LocalDateTime createdAt = LocalDateTime.now();

        //when
        EventDomain eventDomain = EventDomain.of(id, name, description, status, eventDate, createdAt);

        //then
        assertThat(eventDomain.getId()).isEqualTo(id);
        assertThat(eventDomain.getName()).isEqualTo(name);
        assertThat(eventDomain.getDescription()).isEqualTo(description);
        assertThat(eventDomain.getStatus()).isEqualTo(status);
        assertThat(eventDomain.getEventDate()).isEqualTo(eventDate);
        assertThat(eventDomain.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("이벤트 상태 변경 - SCHEDULED에서 OPEN으로")
    void changeStatus_ScheduledToOpen() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.SCHEDULED);

        //when
        eventDomain.changeStatus(EventStatus.OPEN);

        //then
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.OPEN);
    }

    @Test
    @DisplayName("이벤트 상태 변경 - OPEN에서 CLOSED로")
    void changeStatus_OpenToClosed() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);
        eventDomain.changeStatus(EventStatus.OPEN);

        //when
        eventDomain.changeStatus(EventStatus.CLOSED);

        //then
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.CLOSED);
    }

    @Test
    @DisplayName("이벤트 상태 변경 실패 - CLOSED 상태에서는 변경 불가")
    void changeStatus_FailureFromClosed() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);
        eventDomain.changeStatus(EventStatus.OPEN);
        eventDomain.changeStatus(EventStatus.CLOSED);

        //when & then
        assertThatThrownBy(() -> eventDomain.changeStatus(EventStatus.OPEN))
                .isInstanceOf(InvalidEventStatusException.class);
    }

    @Test
    @DisplayName("동일한 상태로 변경 시 예외 발생하지 않음")
    void changeStatus_SameStatus() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);

        //when & then
        assertThatCode(() -> eventDomain.changeStatus(EventStatus.SCHEDULED))
                .doesNotThrowAnyException();

        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.SCHEDULED);
    }

    @Test
    @DisplayName("이벤트 참가 가능 여부 - OPEN 상태일 때만 가능")
    void canApply() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);

        //SCHEDULED 상태 - 참가 불가
        assertThat(eventDomain.canApply()).isFalse();

        //OPEN 상태 - 참가 가능
        eventDomain.changeStatus(EventStatus.OPEN);
        assertThat(eventDomain.canApply()).isTrue();

        //CLOSED 상태 - 참가 불가
        eventDomain.changeStatus(EventStatus.CLOSED);
        assertThat(eventDomain.canApply()).isFalse();
    }

    @Test
    @DisplayName("이벤트 참가 가능 검증 성공")
    void validateCanApply_Success() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);
        eventDomain.changeStatus(EventStatus.OPEN);

        //when & then
        assertThatCode(eventDomain::validateCanApply)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이벤트 참가 가능 검증 실패")
    void validateCanApply_Failure() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);
        // SCHEDULED 상태 (기본값)

        //when & then
        assertThatThrownBy(eventDomain::validateCanApply)
                .isInstanceOf(EventNotOpenedException.class);
    }


    @Test
    @DisplayName("상태 변경 검증 - 유효한 상태 변경")
    void validateStatusChange_ValidChange() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);

        //when & then
        assertThatCode(() -> eventDomain.validateStatusChange(EventStatus.OPEN))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상태 변경 검증 - CLOSED 상태에서 변경 시도")
    void validateStatusChange_FromClosedStatus() {
        //given
        EventDomain eventDomain = EventDomain.create(name, description, eventDate);
        eventDomain.changeStatus(EventStatus.OPEN);
        eventDomain.changeStatus(EventStatus.CLOSED);

        //when & then
        assertThatThrownBy(() -> eventDomain.validateStatusChange(EventStatus.OPEN))
                .isInstanceOf(InvalidEventStatusException.class);
    }
}