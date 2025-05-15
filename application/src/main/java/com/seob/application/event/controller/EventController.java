package com.seob.application.event.controller;

import com.seob.application.common.dto.PageResponse;
import com.seob.application.event.dto.EventCreateRequestDto;
import com.seob.application.event.dto.EventResponseDto;
import com.seob.application.event.dto.EventStatusUpdateRequestDto;
import com.seob.application.event.service.EventApplicationService;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "이벤트", description = "이벤트 관리 API")
public class EventController {
    private final EventApplicationService eventApplicationService;
    
    @Operation(
        summary = "이벤트 생성",
        description = "새로운 이벤트를 생성합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "이벤트 생성 성공",
                         content = @Content(schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
        }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventCreateRequestDto requestDto) {
        EventResponseDto responseDto = eventApplicationService.createEvent(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    @Operation(
        summary = "이벤트 상태 업데이트",
        description = "지정된 이벤트의 상태를 업데이트합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "상태 업데이트 성공", 
                         content = @Content(schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
        }
    )
    @PatchMapping("/{eventId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDto> updateEventStatus(
        @PathVariable Long eventId,
        @RequestBody EventStatusUpdateRequestDto requestDto
    ) {
        EventResponseDto responseDto = eventApplicationService.updateEventStatus(eventId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
    
    @Operation(
        summary = "모든 이벤트 조회",
        description = "모든 이벤트 목록을 페이지네이션과 함께 조회합니다. 상태 및 날짜로 필터링 가능합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "이벤트 목록 조회 성공")
        }
    )
    @GetMapping
    public ResponseEntity<PageResponse<EventResponseDto>> getAllEvents(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @PageableDefault(size = 20, sort = "eventDate", direction = DESC) Pageable pageable) {
        Page<EventResponseDto> events = eventApplicationService.getAllEvents(status, fromDate, toDate, pageable);
        return ResponseEntity.ok(PageResponse.of(events));
    }
    
    @Operation(
        summary = "이벤트 상세 조회",
        description = "특정 ID의 이벤트 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "이벤트 조회 성공", 
                         content = @Content(schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
        }
    )
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long eventId) {
        EventResponseDto event = eventApplicationService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }
    
    @Operation(
        summary = "이벤트 표시 정보 조회",
        description = "특정 이벤트의 표시용 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "이벤트 표시 정보 조회 성공", 
                         content = @Content(schema = @Schema(implementation = EventDisplayInfo.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
        }
    )
    @GetMapping("/{eventId}/display")
    public ResponseEntity<EventDisplayInfo> getEventDisplayInfo(@PathVariable Long eventId) {
        EventDisplayInfo displayInfo = eventApplicationService.getEventDisplayInfo(eventId);
        return ResponseEntity.ok(displayInfo);
    }
}