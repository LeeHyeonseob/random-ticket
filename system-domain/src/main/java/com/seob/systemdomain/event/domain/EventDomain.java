package com.seob.systemdomain.event.domain;

import com.seob.systemdomain.event.exception.EventNotOpenedException;
import com.seob.systemdomain.event.exception.InvalidEventStatusException;
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
    

    public void validateCanApply() {
        if (!canApply()) {
            throw EventNotOpenedException.EXCEPTION;
        }
    }

    public void validateStatusChange(EventStatus newStatus) {
        if (this.status == newStatus) {
            return;
        }

        if (this.status == EventStatus.CLOSED) {
            throw InvalidEventStatusException.EXCEPTION;
        }
    }
    
    // 이벤트 상태 변경
    public void changeStatus(EventStatus newStatus) {
        validateStatusChange(newStatus);
        
        switch (newStatus) {
            case OPEN:
                this.status = EventStatus.OPEN;
                break;
            case CLOSED:
                this.status = EventStatus.CLOSED;
                break;
            case SCHEDULED:
                this.status = EventStatus.SCHEDULED;
                break;
            default:
                throw InvalidEventStatusException.EXCEPTION;
        }
    }

    

}
