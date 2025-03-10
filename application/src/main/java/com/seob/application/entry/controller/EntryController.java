package com.seob.application.entry.controller;

import com.seob.application.auth.CustomUserDetails;
import com.seob.application.entry.controller.dto.EntryCreateRequest;
import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.application.entry.service.EntryApplicationService;
import com.seob.application.entry.service.dto.EventEntryResponse;
import com.seob.application.entry.service.dto.UserEntryResponse;
import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;
    private final EntryApplicationService entryApplicationService;

    //이벤트 참여
    @PostMapping
    public ResponseEntity<EntryResponse> applyForEntry(@RequestBody EntryCreateRequest entryCreateRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserId userId = userDetails.getUserId();

        EntryDomain entryDomain = entryService.apply(userId.getValue(),
                entryCreateRequest.eventId(),
                entryCreateRequest.ticketId()
        );
        return ResponseEntity.ok(EntryResponse.of(entryDomain));
    }
    //사용자
    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventEntryResponse> getEntriesByEventId(@PathVariable("eventId") Long eventId) {
        EventEntryResponse response = entryApplicationService.getEventWithParticipants(eventId);
        return ResponseEntity.ok(response);
    }

    // 사용자의 이벤트 참가 내역 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserEntryResponse>> getEntriesByUserId(@PathVariable String userId) {
        List<UserEntryResponse> responses = entryApplicationService.getUserEntries(userId);
        return ResponseEntity.ok(responses);
    }
}
