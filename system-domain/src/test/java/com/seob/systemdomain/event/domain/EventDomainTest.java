package com.seob.systemdomain.event.domain;

import com.seob.systemdomain.event.vo.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventDomainTest {

    @Test
    @DisplayName("이벤트 생성")
    void createEventDomain() {
        //given
        String name = "오늘_날짜_이벤트";
        String description = "기프티콘 뿌릴거다";

        //when
        EventDomain eventDomain = EventDomain.create(name, description);

        //then
        assertThat(eventDomain).isNotNull();
        assertThat(eventDomain.getName()).isEqualTo(name);
        assertThat(eventDomain.getDescription()).isEqualTo(description);
        assertThat(eventDomain.getStatus()).isEqualTo(EventStatus.SCHEDULED);
        assertThat(eventDomain.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이벤트 응모 가능 검사")
    void check_can_apply(){
        //given
        String name = "오늘_날짜_이벤트";
        String description = "기프티콘 뿌릴거다";

        EventDomain eventDomain = EventDomain.create(name, description);

        //when
        eventDomain.openEvent();

        //then
        assertThat(eventDomain.canApply()).isTrue();


    }




}