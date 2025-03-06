package com.seob.application.event.controller;

import com.seob.application.event.controller.dto.CreateEventRequest;
import com.seob.application.event.controller.dto.EventClientResponse;
import com.seob.application.event.controller.dto.EventResponse;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    //이벤트 생성
    //관리자 권한 체크 로직 체크 필요
    @PostMapping
    public ResponseEntity<EventResponse> createEvents(@RequestBody CreateEventRequest createEventRequest){
        EventDomain eventDomain = eventService.createEvent(
                createEventRequest.name(),
                createEventRequest.description(),
                createEventRequest.eventDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.of(eventDomain));
    }

    //이벤트 상태 변경(이벤트 오픈 및 종료)
    //관리자 권한 체크 로직 추가 필요
    @PutMapping("/{eventId}/status")
    public ResponseEntity<EventResponse> updateEventStatus(@PathVariable Long eventId, @RequestBody String eventStatus) {
        EventDomain eventDomain = eventService.changeStatus(eventId, eventStatus);
        return ResponseEntity.ok(EventResponse.of(eventDomain));
    }

    //단일 이벤트 조회
    @GetMapping("/{eventId}")
    public ResponseEntity<EventClientResponse> getEvent(@PathVariable Long eventId) {
        EventDisplayInfo eventDisplayInfo = eventService.getEventDisplayInfo(eventId);
        return ResponseEntity.ok(EventClientResponse.of(eventDisplayInfo));
    }

    //전체 이벤트 조회 -> 나중에 페이징 개선
    @GetMapping
    public ResponseEntity<List<EventClientResponse>> getAllEvents() {
        List<EventDisplayInfo> eventDisplayInfos = eventService.getEventDisplayInfoList();
        List<EventClientResponse> eventClientResponses = eventDisplayInfos.stream()
                .map(EventClientResponse::of)
                .toList();

        return ResponseEntity.ok(eventClientResponses);
    }





}
