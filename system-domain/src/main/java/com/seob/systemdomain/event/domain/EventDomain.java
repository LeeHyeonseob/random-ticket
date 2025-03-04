package com.seob.systemdomain.event.domain;

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

    public static EventDomain of(String name, String description, EventStatus status, LocalDate eventDate, LocalDateTime createdAt){
        EventDomain eventDomain = new EventDomain();
        eventDomain.name = name;
        eventDomain.description = description;
        eventDomain.status = status;
        eventDomain.eventDate = eventDate;
        eventDomain.createdAt = createdAt;
        return eventDomain;
    }



    public boolean canApply(){
        return status == EventStatus.OPEN;
    }

    public void openEvent(){
        status = EventStatus.OPEN;
    }

    public void closeEvent(){
        status = EventStatus.CLOSED;
    }



}
