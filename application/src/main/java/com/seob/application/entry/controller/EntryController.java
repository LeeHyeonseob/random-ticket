package com.seob.application.entry.controller;

import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.application.entry.controller.dto.ParticipantEntryResponse;
import com.seob.application.entry.controller.dto.UserEntryResponse;
import com.seob.application.entry.service.EntryApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entries")
@RequiredArgsConstructor
@Tag(name = "이벤트 참여", description = "이벤트 참여 내역 관리 API")
public class EntryController {

    private final EntryApplicationService entryApplicationService;

    @Operation(
        summary = "이벤트 참여 신청",
        description = "티켓을 사용해 이벤트에 참여합니다. 티켓 ID를 제공하면 해당 티켓을 사용하고, 제공하지 않으면 자동으로 적합한 티켓을 찾습니다. 인증이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/events/{eventId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntryResponse> applyToEvent(
            @PathVariable Long eventId) {
        
        EntryResponse response = entryApplicationService.applyToEventWithoutTicket(eventId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "내 이벤트 참여 내역 조회", 
        description = "현재 로그인한 사용자의 이벤트 참여 내역을 조회합니다."
    )
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserEntryResponse>> getMyEntries() {
        List<UserEntryResponse> entries = entryApplicationService.getMyEntries();
        return ResponseEntity.ok(entries);
    }

    @Operation(
        summary = "사용자 이벤트 참여 내역 조회", 
        description = "특정 사용자의 이벤트 참여 내역을 조회합니다. 관리자 권한 필요."
    )
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserEntryResponse>> getUserEntries(@PathVariable String userId) {
        List<UserEntryResponse> entries = entryApplicationService.getUserEntries(userId);
        return ResponseEntity.ok(entries);
    }

    @Operation(
        summary = "이벤트 참여 내역 조회", 
        description = "특정 이벤트의 모든 참여 내역을 조회합니다. 관리자 권한 필요."
    )
    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParticipantEntryResponse>> getEventEntries(@PathVariable Long eventId) {
        List<ParticipantEntryResponse> entries = entryApplicationService.getEventEntries(eventId);
        return ResponseEntity.ok(entries);
    }
}
