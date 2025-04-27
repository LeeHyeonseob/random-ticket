package com.seob.systemdomain.event.domain;

import com.seob.systemdomain.event.exception.EventNotFoundException;
import com.seob.systemdomain.event.vo.EventStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventDomain {

    private Long id;
    private String name;
    private String description;
    private EventStatus status;
    private LocalDate eventDate;
    private LocalDateTime createdAt;

    public static EventDomain create(String name, String description, LocalDate eventDate){
        EventDomain eventDomain = new EventDomain();
        eventDomain.name = name;
        eventDomain.description = description;
        eventDomain.eventDate = eventDate;
        eventDomain.status = EventStatus.SCHEDULED;
        eventDomain.createdAt = LocalDateTime.now();
        return eventDomain;
    }

    public static EventDomain of(Long id,String name, String description, EventStatus status, LocalDate eventDate, LocalDateTime createdAt){
        EventDomain eventDomain = new EventDomain();
        eventDomain.id = id;
        eventDomain.name = name;
        eventDomain.description = description;
        eventDomain.status = status;
        eventDomain.eventDate = eventDate;
        eventDomain.createdAt = createdAt;
        return eventDomain;
    }

    //이벤트 참가 가능 여부 확인
    public boolean canApply(){
        return status == EventStatus.OPEN;
    }

    //이벤트 오픈 상태로 변경
    public void openEvent(){
        status = EventStatus.OPEN;
    }

    // 이벤트 마감 상태로 변경
    public void closeEvent(){
        status = EventStatus.CLOSED;
    }
    
    // 이벤트 상태 변경
    public void changeStatus(EventStatus newStatus) {
        switch (newStatus) {
            case OPEN:
                openEvent();
                break;
            case CLOSED:
                closeEvent();
                break;
            case SCHEDULED:
                // SCHEDULED 상태로 변경
                this.status = EventStatus.SCHEDULED;
                break;
            default:

                break;
        }
    }
    
    // 이벤트 종료 처리 필요 여부 확인
    public boolean shouldBeClosed(LocalDate currentDate) {
        return eventDate.isBefore(currentDate) && status != EventStatus.CLOSED;
    }
}
