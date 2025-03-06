package com.seob.application.entry.controller;

import com.seob.application.entry.controller.dto.EntryCreateRequest;
import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.application.entry.service.EventEntryFacadeService;
import com.seob.application.entry.service.UserEntryFacadeService;
import com.seob.application.entry.service.dto.EventEntryResponse;
import com.seob.application.entry.service.dto.UserEntryResponse;
import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;
    private final EventEntryFacadeService eventEntryFacadeService;
    private final UserEntryFacadeService userEntryFacadeService;

    //이벤트 참여
    @PostMapping
    public ResponseEntity<EntryResponse> applyForEntry(@RequestBody EntryCreateRequest entryCreateRequest) {
        EntryDomain entryDomain = entryService.apply(entryCreateRequest.userId(),
                entryCreateRequest.eventId(),
                entryCreateRequest.ticketId()
        );
        return ResponseEntity.ok(EntryResponse.of(entryDomain));
    }
    //사용자
    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventEntryResponse> getEntriesByEventId(@PathVariable("eventId") Long eventId) {
        EventEntryResponse response = eventEntryFacadeService.getEventWithParticipants(eventId);
        return ResponseEntity.ok(response);
    }

    // 사용자의 이벤트 참가 내역 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserEntryResponse>> getEntriesByUserId(@PathVariable String userId) {
        List<UserEntryResponse> responses = userEntryFacadeService.getUserEntries(userId);
        return ResponseEntity.ok(responses);
    }
}
