package com.seob.application.event.scheduler;

import com.seob.systemdomain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class EventScheduler {

    private final EventService eventService;

    //전날 이벤트 닫기
    @Scheduled(cron = "0 0 0 * * *")
    public void closeYesterdayEvent(){
        eventService.closeYesterdayEvents();
    }
}
