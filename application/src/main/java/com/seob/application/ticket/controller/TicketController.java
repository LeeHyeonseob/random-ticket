package com.seob.application.ticket.controller;

import com.seob.application.ticket.controller.dto.TicketResponseDto;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.service.TicketService;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/issue")
    public ResponseEntity<TicketResponseDto> issueTicket(@RequestParam String userId){
        TicketDomain ticketDomain = ticketService.issueTicket(UserId.of(userId));

        return ResponseEntity.ok(TicketResponseDto.of(ticketDomain));
    }


}
