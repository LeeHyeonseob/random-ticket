package com.seob.application.ticket.controller;

import com.seob.application.auth.CustomUserDetails;
import com.seob.application.ticket.controller.dto.TicketResponseDto;
import com.seob.application.ticket.service.TicketApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 티켓 관련 API 컨트롤러
 * 티켓 발급 기능을 제공합니다
 */
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Tag(name = "티켓", description = "티켓 발급 API")
public class TicketController {
    private final TicketApplicationService ticketApplicationService;
    
    /**
     * 티켓 발급 API
     * 사용자에게 새 티켓을 발급합니다
     * @param userDetails 인증된 사용자 정보
     * @return 발급된 티켓 정보
     */
    @Operation(
        summary = "티켓 발급",
        description = "로그인한 사용자에게 새 티켓을 발급합니다. 인증이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "티켓 발급 성공", 
                        content = @Content(schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "429", description = "티켓 발급 한도 초과")
        }
    )
    @PostMapping("/issue")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponseDto> issueTicket(@AuthenticationPrincipal CustomUserDetails userDetails) {
        TicketResponseDto responseDto = ticketApplicationService.issueTicket(userDetails);
        return ResponseEntity.ok(responseDto);
    }
}
